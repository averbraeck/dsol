package nl.tudelft.simulation.jstats.math;

import org.djutils.exceptions.Throw;

/**
 * The ProbMath class defines some very basic probabilistic mathematical functions.
 * <p>
 * Copyright (c) 2004-2022 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://simulation.tudelft.nl/" target="_blank"> https://simulation.tudelft.nl</a>. The DSOL
 * project is distributed under a three-clause BSD-style license, which can be found at
 * <a href="https://simulation.tudelft.nl/dsol/3.0/license.html" target="_blank">
 * https://simulation.tudelft.nl/dsol/3.0/license.html</a>.
 * </p>
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank"> Alexander Verbraeck</a>
 */
public final class ProbMath
{
    /** stored values of n! as a double value. */
    public static final double[] FACTORIAL_DOUBLE;

    /** stored values of n! as a long value. */
    public static final long[] FACTORIAL_LONG;

    /** stored values of a^n as a double value. */
    public static final double[] POW2_DOUBLE;

    /** stored values of 2^n as a long value. */
    public static final long[] POW2_LONG;

    /** calculate n! and 2^n. */
    static
    {
        FACTORIAL_DOUBLE = new double[171];
        FACTORIAL_DOUBLE[0] = 1.0;
        double d = 1.0;
        for (int i = 1; i <= 170; i++)
        {
            d = d * i;
            FACTORIAL_DOUBLE[i] = d;
        }

        FACTORIAL_LONG = new long[21];
        FACTORIAL_LONG[0] = 1L;
        long n = 1L;
        for (int i = 1; i <= 20; i++)
        {
            n = n * i;
            FACTORIAL_LONG[i] = n;
        }

        POW2_DOUBLE = new double[1024];
        POW2_DOUBLE[0] = 1.0;
        for (int i = 1; i < 1024; i++)
        {
            POW2_DOUBLE[i] = 2.0 * POW2_DOUBLE[i - 1];
        }

        POW2_LONG = new long[63];
        POW2_LONG[0] = 1L;
        for (int i = 1; i < 63; i++)
        {
            POW2_LONG[i] = 2L * POW2_LONG[i - 1];
        }
    }

    /**
     * Utility class.
     */
    private ProbMath()
    {
        // unreachable code for the utility class
    }

    /**
     * Compute n factorial as a double. fac(n) = n * (n-1) * (n-2) * ... 2 * 1.
     * @param n int; the value to calculate n! for
     * @return double; n factorial
     */
    public static double factorial(final int n)
    {
        Throw.when(n < 0, IllegalArgumentException.class, "n! with n<0 is invalid");
        Throw.when(n > 170, IllegalArgumentException.class, "n! with n>170 is larger than Double.MAX_VALUE");
        return FACTORIAL_DOUBLE[n];
    }

    /**
     * Compute n factorial as a double. fac(n) = n * (n-1) * (n-2) * ... 2 * 1.
     * @param n long; the value to calculate n! for
     * @return double; n factorial
     */
    public static double factorial(final long n)
    {
        Throw.when(n < 0, IllegalArgumentException.class, "n! with n<0 is invalid");
        Throw.when(n > 170, IllegalArgumentException.class, "n! with n>170 is larger than Double.MAX_VALUE");
        return FACTORIAL_DOUBLE[(int) n];
    }

    /**
     * Compute n factorial as a long. fac(n) = n * (n-1) * (n-2) * ... 2 * 1.
     * @param n int; the value to calculate n! for
     * @return double; n factorial
     */
    public static long fac(final int n)
    {
        Throw.when(n < 0, IllegalArgumentException.class, "n! with n<0 is invalid");
        Throw.when(n > 20, IllegalArgumentException.class, "n! with n>20 is more than 64 bits long");
        return FACTORIAL_LONG[n];
    }

    /**
     * Compute n factorial as a long. fac(n) = n * (n-1) * (n-2) * ... 2 * 1.
     * @param n long; the value to calculate n! for
     * @return double; n factorial
     */
    public static long fac(final long n)
    {
        Throw.when(n < 0, IllegalArgumentException.class, "n! with n<0 is invalid");
        Throw.when(n > 20, IllegalArgumentException.class, "n! with n>20 is more than 64 bits long");
        return FACTORIAL_LONG[(int) n];
    }

    /**
     * Compute the k-permutations of n.
     * @param n int; the first parameter
     * @param k int; the second parameter
     * @return the k-permutations of n
     */
    public static double permutations(final int n, final int k)
    {
        if (k > n)
        {
            throw new IllegalArgumentException("permutations of (n,k) with k>n");
        }
        return factorial(n) / factorial(n - k);
    }

    /**
     * Compute the k-permutations of n.
     * @param n long; the first parameter
     * @param k long; the second parameter
     * @return the k-permutations of n
     */
    public static double permutations(final long n, final long k)
    {
        if (k > n)
        {
            throw new IllegalArgumentException("permutations of (n,k) with k>n");
        }
        return factorial(n) / factorial(n - k);
    }

    /**
     * Computes the k-permutations of n as a long.
     * @param n int; the first parameter
     * @param k int; the second parameter
     * @return the k-permutations of n
     */
    public static long perm(final int n, final int k)
    {
        if (k > n)
        {
            throw new IllegalArgumentException("permutations of (n,k) with k>n");
        }
        return fac(n) / fac(n - k);
    }

    /**
     * Computes the k-permutations of n as a long.
     * @param n long; the first parameter
     * @param k long; the second parameter
     * @return the k-permutations of n
     */
    public static long perm(final long n, final long k)
    {
        if (k > n)
        {
            throw new IllegalArgumentException("permutations of (n,k) with k>n");
        }
        return fac(n) / fac(n - k);
    }

    /**
     * computes the combinations of n over k.
     * @param n int; the first parameter
     * @param k int; the second parameter
     * @return the combinations of n over k
     */
    public static double combinations(final int n, final int k)
    {
        if (k > n)
        {
            throw new IllegalArgumentException("combinations of (n,k) with k>n");
        }
        return factorial(n) / (factorial(k) * factorial(n - k));
    }

    /**
     * computes the combinations of n over k.
     * @param n long; the first parameter
     * @param k long; the second parameter
     * @return the combinations of n over k
     */
    public static double combinations(final long n, final long k)
    {
        if (k > n)
        {
            throw new IllegalArgumentException("combinations of (n,k) with k>n");
        }
        return factorial(n) / (factorial(k) * factorial(n - k));
    }

    /**
     * computes the combinations of n over k as a long.
     * @param n int; the first parameter
     * @param k int; the second parameter
     * @return the combinations of n over k
     */
    public static long comb(final int n, final int k)
    {
        if (k > n)
        {
            throw new IllegalArgumentException("combinations of (n,k) with k>n");
        }
        return fac(n) / (fac(k) * fac(n - k));
    }

    /**
     * computes the combinations of n over k as a long.
     * @param n long; the first parameter
     * @param k long; the second parameter
     * @return the combinations of n over k
     */
    public static long comb(final long n, final long k)
    {
        if (k > n)
        {
            throw new IllegalArgumentException("combinations of (n,k) with k>n");
        }
        return fac(n) / (fac(k) * fac(n - k));
    }

    /**
     * Approximates erf(z) using a Taylor series.<br>
     * The Taylor series for erf(z) for abs(z) <u>&lt;</u> 0.5 that is used is:<br>
     * &nbsp; &nbsp; erf(z) = (exp(-z<sup>2</sup>) / &radic;&pi;) &Sigma; [ 2z<sup>2n + 1</sup> / (2n + 1)!!]<br>
     * The Taylor series for erf(z) for abs(z) &gt; 3.7 that is used is:<br>
     * &nbsp; &nbsp; erf(z) = 1 - (exp(-z<sup>2</sup>) / &radic;&pi;) &Sigma; [ (-1)<sup>n</sup> (2n - 1)!! z<sup>-(2n +
     * 1)</sup> / 2<sup>n</sup>]<br>
     * See <a href="https://mathworld.wolfram.com/Erf.html">https://mathworld.wolfram.com/Erf.html</a>. <br>
     * For 0.5 &lt; z &lt; 3.7 it approximates erf(z) using the following Taylor series:<br>
     * &nbsp; &nbsp; erf(z) = (2/&radic;&pi;) (z - z<sup>3</sup>/3 + z<sup>5</sup>/10 - z<sup>7</sup>/42 + z<sup>9</sup>/216 -
     * ...)<br>
     * The factors are given by <a href="https://oeis.org/A007680">https://oeis.org/A007680</a>, which evaluates to a(n) =
     * (2n+1)n!. See <a href="https://en.wikipedia.org/wiki/Error_function">https://en.wikipedia.org/wiki/Error_function</a>.
     * @param z double; the value to calculate the error function for
     * @return erf(z)
     */
    public static double erf(final double z)
    {
        double zpos = Math.abs(z);
        if (zpos < 0.5)
        {
            return erfSmall(z);
        }
        if (zpos > 3.8)
        {
            return erfBig(z);
        }
        return erfTaylor(z);
    }

    /**
     * The Taylor series for erf(z) for abs(z) <u>&lt;</u> 0.5 that is used is:<br>
     * &nbsp; &nbsp; erf(z) = (exp(-z<sup>2</sup>) / &radic;&pi;) &Sigma; [ 2z<sup>2n + 1</sup> / (2n + 1)!!]<br>
     * where the !! operator is the 'double factorial' operator which is (n).(n-2)...8.4.2 for even n, and (n).(n-2)...3.5.1 for
     * odd n. See <a href="https://mathworld.wolfram.com/Erf.html">https://mathworld.wolfram.com/Erf.html</a> formula (9) and
     * (10). This function would work well for z <u>&lt;</u> 0.5.
     * @param z double; the parameter
     * @return double; erf(x)
     */
    private static double erfSmall(final double z)
    {
        double zpos = Math.abs(z);
        // @formatter:off
        double sum = zpos 
                + 2.0 * Math.pow(zpos, 3) / 3.0 
                + 4.0 * Math.pow(zpos, 5) / 15.0 
                + 8.0 * Math.pow(zpos, 7) / 105.0
                + 16.0 * Math.pow(zpos, 9) / 945.0 
                + 32.0 * Math.pow(zpos, 11) / 10395.0
                + 64.0 * Math.pow(zpos, 13) / 135135.0 
                + 128.0 * Math.pow(zpos, 15) / 2027025.0
                + 256.0 * Math.pow(zpos, 17) / 34459425.0 
                + 512.0 * Math.pow(zpos, 19) / 654729075.0
                + 1024.0 * Math.pow(zpos, 21) / 13749310575.0;
        // @formatter:on
        return Math.signum(z) * sum * 2.0 * Math.exp(-zpos * zpos) / Math.sqrt(Math.PI);
    }

    /**
     * Calculate erf(z) for large values using the Taylor series:<br>
     * &nbsp; &nbsp; erf(z) = 1 - (exp(-z<sup>2</sup>) / &radic;&pi;) &Sigma; [ (-1)<sup>n</sup> (2n - 1)!! z<sup>-(2n +
     * 1)</sup> / 2<sup>n</sup>]<br>
     * where the !! operator is the 'double factorial' operator which is (n).(n-2)...8.4.2 for even n, and (n).(n-2)...3.5.1 for
     * odd n. See <a href="https://mathworld.wolfram.com/Erf.html">https://mathworld.wolfram.com/Erf.html</a> formula (18) to
     * (20). This function would work well for z <u>&gt;</u> 3.7.
     * @param z double; the argument
     * @return double; erf(z)
     */
    private static double erfBig(final double z)
    {
        double zpos = Math.abs(z);
        // @formatter:off
        double sum = 1.0 / zpos 
                - (1.0 / 2.0) * Math.pow(zpos, -3) 
                + (3.0 / 4.0) * Math.pow(zpos, -5)
                - (15.0 / 8.0) * Math.pow(zpos, -7) 
                + (105.0 / 16.0) * Math.pow(zpos, -9) 
                - (945.0 / 32.0) * Math.pow(zpos, -11)
                + (10395.0 / 64.0) * Math.pow(zpos, -13) 
                - (135135.0 / 128.0) * Math.pow(zpos, -15)
                + (2027025.0 / 256.0) * Math.pow(zpos, -17);
        // @formatter:on
        return Math.signum(z) * (1.0 - sum * Math.exp(-zpos * zpos) / Math.sqrt(Math.PI));
    }

    /**
     * Calculate erf(z) using the Taylor series:<br>
     * &nbsp; &nbsp; erf(z) = (2/&radic;&pi;) (z - z<sup>3</sup>/3 + z<sup>5</sup>/10 - z<sup>7</sup>/42 + z<sup>9</sup>/216 -
     * ...)<br>
     * The factors are given by <a href="https://oeis.org/A007680">https://oeis.org/A007680</a>, which evaluates to a(n) =
     * (2n+1)n!. See <a href="https://en.wikipedia.org/wiki/Error_function">https://en.wikipedia.org/wiki/Error_function</a>.
     * This works pretty well on the interval [0.5,3.7].
     * @param z double; the argument
     * @return double; erf(z)
     */
    private static double erfTaylor(final double z)
    {
        double zpos = Math.abs(z);
        double d = zpos;
        double zpow = zpos;
        for (int i = 1; i < 64; i++) // max 64 steps
        {
            // calculate Math.pow(zpos, 2 * i + 1) / ((2 * i + 1) * factorial(i));
            zpow *= zpos * zpos;
            double term = zpow / ((2.0 * i + 1.0) * ProbMath.factorial(i));
            d += term * ((i & 1) == 0 ? 1 : -1);
            if (term < 1E-16)
            {
                break;
            }
        }
        return Math.signum(z) * d * 2.0 / Math.sqrt(Math.PI);
    }

    /**
     * Approximates erf<sup>-1</sup>(p) based on
     * <a href="http://www.naic.edu/~jeffh/inverse_cerf.c">http://www.naic.edu/~jeffh/inverse_cerf.c</a> code.
     * @param y double; the cumulative probability to calculate the inverse error function for
     * @return erf<sup>-1</sup>(p)
     */
    public static double erfInv(final double y)
    {
        double ax, t, ret;
        ax = Math.abs(y);

        /*
         * This approximation, taken from Table 10 of Blair et al., is valid for |x|<=0.75 and has a maximum relative error of
         * 4.47 x 10^-8.
         */
        if (ax <= 0.75)
        {

            double[] p = new double[] {-13.0959967422, 26.785225760, -9.289057635};
            double[] q = new double[] {-12.0749426297, 30.960614529, -17.149977991, 1.00000000};

            t = ax * ax - 0.75 * 0.75;
            ret = ax * (p[0] + t * (p[1] + t * p[2])) / (q[0] + t * (q[1] + t * (q[2] + t * q[3])));

        }
        else if (ax >= 0.75 && ax <= 0.9375)
        {
            double[] p = new double[] {-.12402565221, 1.0688059574, -1.9594556078, .4230581357};
            double[] q = new double[] {-.08827697997, .8900743359, -2.1757031196, 1.0000000000};

            /*
             * This approximation, taken from Table 29 of Blair et al., is valid for .75<=|x|<=.9375 and has a maximum relative
             * error of 4.17 x 10^-8.
             */
            t = ax * ax - 0.9375 * 0.9375;
            ret = ax * (p[0] + t * (p[1] + t * (p[2] + t * p[3]))) / (q[0] + t * (q[1] + t * (q[2] + t * q[3])));

        }
        else if (ax >= 0.9375 && ax <= (1.0 - 1.0e-9))
        {
            double[] p =
                    new double[] {.1550470003116, 1.382719649631, .690969348887, -1.128081391617, .680544246825, -.16444156791};
            double[] q = new double[] {.155024849822, 1.385228141995, 1.000000000000};

            /*
             * This approximation, taken from Table 50 of Blair et al., is valid for .9375<=|x|<=1-10^-100 and has a maximum
             * relative error of 2.45 x 10^-8.
             */
            t = 1.0 / Math.sqrt(-Math.log(1.0 - ax));
            ret = (p[0] / t + p[1] + t * (p[2] + t * (p[3] + t * (p[4] + t * p[5])))) / (q[0] + t * (q[1] + t * (q[2])));
        }
        else
        {
            ret = Double.POSITIVE_INFINITY;
        }

        return Math.signum(y) * ret;
    }

    /** Coefficients for the ln(gamma(x)) function. */
    private static final double[] GAMMALN_COF = {76.18009172947146, -86.50532032941677, 24.01409824083091, -1.231739572450155,
            0.1208650973866179e-2, -0.5395239384953e-5};

    /**
     * Calculates ln(gamma(x)). Java version of gammln function in Numerical Recipes in C, p.214.
     * @param xx double; the value to calculate the gamma function for
     * @return double; gamma(x)
     * @throws IllegalArgumentException when x is &lt; 0
     */
    public static double gammaln(final double xx)
    {
        Throw.when(xx < 0, IllegalArgumentException.class, "gamma function not defined for real values < 0");
        double x, y, tmp, ser;
        x = xx;
        y = x;
        tmp = x + 5.5;
        tmp -= (x + 0.5) * Math.log(tmp);
        ser = 1.000000000190015;
        for (int j = 0; j <= 5; j++)
        {
            ser += GAMMALN_COF[j] / ++y;
        }
        return -tmp + Math.log(2.5066282746310005 * ser / x);
    }

    /**
     * Calculates gamma(x). Based on the gammln function in Numerical Recipes in C, p.214.
     * @param x double; the value to calculate the gamma function for
     * @return double; gamma(x)
     * @throws IllegalArgumentException when x is &lt; 0
     */
    public static double gamma(final double x)
    {
        return Math.exp(gammaln(x));
    }

    /**
     * Calculates Beta(z, w) where Beta(z, w) = &Gamma;(z) &Gamma;(w) / &Gamma;(z + w).
     * @param z double; beta function parameter 1
     * @param w ; beta function parameter 2
     * @return double; beta(z, w)
     * @throws IllegalArgumentException when one of the parameters is &lt; 0
     */
    public static double beta(final double z, final double w)
    {
        Throw.when(z < 0 || w < 0, IllegalArgumentException.class, "beta function not defined for negative arguments");
        return Math.exp(gammaln(z) + gammaln(w) - gammaln(z + w));
    }

}
