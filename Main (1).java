import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Math;
import java.util.Random;

public class Main {
    static BigInteger zero = new BigInteger("0");
    static BigInteger one = new BigInteger("1");
    static BigInteger two = new BigInteger("2");

    static BigInteger[] extendedEuclidean(BigInteger a, BigInteger b) {
        BigInteger[] result = {a, one, zero};

        if (!b.equals(zero)){
            BigInteger[] tempResult = extendedEuclidean(b, a.mod(b));
            result[0] = tempResult[0];
            result[1] = tempResult[2];
            result[2] = tempResult[1].subtract(tempResult[2].multiply(a.divide(b)));
        }

        return result;
    }

    static BigInteger modPower(BigInteger a, BigInteger b, BigInteger m){
        BigInteger r = new BigInteger("1");

        while(b.compareTo(zero) > 0){
            if(b.mod(two).equals(one)){
                r = r.multiply(a).mod(m);
            }
            a = a.pow(2).mod(m);
            b = b.shiftRight(1);
        }
        return r;
    }

    static boolean millerTest(BigInteger p, BigInteger a){
        BigInteger tempP = p.subtract(one);
        BigInteger d;
        int s = 0;

        while(true){
            if(!tempP.testBit(0)){
                tempP = tempP.divide(two);
                s += 1;
            }
            else {
                d = tempP;
                break;
            }
        }
        if (modPower(a,d,p).equals(one))
            return true;
        else{
            for(int i=0;i<=s;i++)
                if(modPower(a,new BigInteger("2").pow(i).multiply(d),p).equals(p.subtract(one)))
                    return true;
        }
        return false;
    }

    static boolean multiMillerTest(BigInteger p, int k,ArrayList<Integer> primes){
        BigInteger bigPrime;
        for (Integer prime : primes) {
            bigPrime = BigInteger.valueOf(prime);
            if (p.mod(bigPrime).equals(zero))
                return false;
            if (bigPrime.compareTo(p.sqrt()) > 0)
                break;
        }

        BigInteger a;
        while (k>0){
            k -= 1;
            a = randomBigInteger(p);
//            System.out.println(a);
            if(!millerTest(p,a))
                return false;
        }
        return true;
    }

    static String chineseRemainder(String cipher, BigInteger n, BigInteger d, BigInteger p, BigInteger q){
        StringBuilder message = new StringBuilder();
        String[] cipherChars = cipher.split(" ");
        BigInteger dp = d.mod(p.subtract(one));
        BigInteger dq = d.mod(q.subtract(one));
        BigInteger[] eea = extendedEuclidean(p,q);
        BigInteger mp;
        BigInteger mq;
        BigInteger cInt;
        for(String c:cipherChars){
            cInt = new BigInteger(c);
            mp = modPower(cInt,dp,p);
            mq = modPower(cInt,dq,q);
            message.append((char) mp.multiply(eea[2]).multiply(q).add(mq.multiply(eea[1]).multiply(p)).mod(n).intValue());
        }
        return message.toString();
    }

    static String rsaEncrypt(String message, BigInteger n, BigInteger e){
        StringBuilder cipher = new StringBuilder();
        for (int i=0; i<message.length(); i++){
            int m = message.charAt(i);
            cipher.append(modPower(BigInteger.valueOf(m), e, n)).append(" ");
        }
        return cipher.toString();
    }

    static String rsaDecrypt(String cipher, BigInteger n, BigInteger d){
        StringBuilder message = new StringBuilder();
        String[] cipherChars = cipher.split(" ");
        for(String c:cipherChars){
            message.append((char) modPower(new BigInteger(c), d, n).intValue());
        }
        return message.toString();
    }

    static BigInteger[] rsaKeysGenerator(ArrayList<Integer> primes){
        BigInteger p = bigPrimeGenerator(primes);
        BigInteger q = bigPrimeGenerator(primes);

        System.out.println("p="+p+"\nq="+q);

        BigInteger n = p.multiply(q);
        BigInteger phi = p.subtract(one).multiply(q.subtract(one));
        BigInteger[] eea;
        BigInteger e;
        do {
            e = new BigInteger(phi.bitLength(), new Random());
            eea = extendedEuclidean(e,phi);
        } while (e.compareTo(phi) >= 0 || e.compareTo(two) < 0 || !eea[0].equals(one));

        BigInteger d = eea[1].mod(phi);

        return new BigInteger[]{n, e, d, p, q};
    }

    static BigInteger randomBigInteger(BigInteger upperLimit){
        BigInteger randomNumber;
        BigInteger[] eea;
        do {
            randomNumber = new BigInteger(upperLimit.bitLength(), new Random());
            eea = extendedEuclidean(randomNumber,upperLimit);
        } while (randomNumber.compareTo(upperLimit) >= 0 || randomNumber.compareTo(two) < 0 || !eea[0].equals(one));
        return randomNumber;
    }

    static BigInteger bigPrimeGenerator(ArrayList<Integer> primes){
        BigInteger randomNumber;
        do {
            randomNumber = new BigInteger(1024, new Random());
        } while (randomNumber.compareTo(two) < 0 || !multiMillerTest(randomNumber, 4,primes));
        return randomNumber;
    }

    static ArrayList<Integer> sieveOfEratosthenes(int n){
        boolean[] prime = new boolean[n + 1];
        for (int i = 0; i <= n; i++)
            prime[i] = true;
        for (int p = 2; p * p <= n; p++) {
            if (prime[p]) {
                for (int i = p * p; i <= n; i += p)
                    prime[i] = false;
            }
        }
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 2; i <= n; i++) {
            if (prime[i])
                result.add(i);
        }
        return result;
    }

    public static void main(String[] args) {
        BigInteger num1 = new BigInteger("12");
        BigInteger num2 = new BigInteger("213");
        BigInteger num3 = new BigInteger("25261");

        //testing the extended euclidean algorithm:
        System.out.println("GCD, x and y are " + Arrays.toString(extendedEuclidean(num1, num2)) + " respectively");

        //testing the fast modular exponentiation algorithm
        System.out.println("the final result of Fast modular exponentiation is " + modPower(num1, num2, num3));

        //testing out the Miller-Rabin algorithm
        if(!millerTest(num1, num2)){
            System.out.println("Composite Number");
        } else {
            System.out.println("Possible Prime Number");
        }

        //testing out the RSA algorithm
        ArrayList<Integer> primes = sieveOfEratosthenes((int)Math.pow(10,3));
        BigInteger[] RSAkeys = rsaKeysGenerator(primes);

        String message = """
                tfyytfytfyfytfytfytfyfytfytfytfytfytfytftyfxfvnohiuho
                """;
        String cipher = rsaEncrypt(message,RSAkeys[0],RSAkeys[1]);
        System.out.println("encrypted message= "+cipher);

        String decryptedMessage = chineseRemainder(cipher,RSAkeys[0],RSAkeys[2],RSAkeys[3],RSAkeys[4]);
        System.out.println("decrypted message= "+decryptedMessage);
    }
}