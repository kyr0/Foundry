/*
 * File:                OverconstrainedConjugateGradientMatrixMinimizer.java
 * Authors:             Jeremy D. Wendt
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright 2016, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 */

package gov.sandia.cognition.learning.algorithm.minimization.matrix;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.CloneableSerializable;

/**
 * Implements a overconstrained conjugate gradient matrix optimizer.
 * 
 * @author Jeremy D. Wendt
 * @since 4.0.0
 */
@PublicationReference(author = "Jonathan Richard Shewchuk",
    title = "An Introduction to the Conjugate Gradient Method Without the Agonizing Pain",
    type = PublicationType.WebPage,
    year = 1994,
    url = "http://www.cs.cmu.edu/~quake-papers/painless-conjugate-gradient.pdf‎")
public class OverconstrainedConjugateGradientMatrixMinimizer
    extends IterativeMatrixSolver<OverconstrainedMatrixVectorMultiplier>
{

    /**
     * The matrix vector multiplier
     */
    private OverconstrainedMatrixVectorMultiplier A;

    /**
     * The per-iteration residual
     */
    private Vector residual;

    /**
     * The conjugated direction vector
     */
    private Vector d;

    /**
     * The current best estimate for x
     */
    private Vector x;

    /**
     * A cross-iteration variable
     */
    private double delta;
    
    /**
     * My A^T*b right-hand-side vector
     */
    private Vector AtransB;

    /**
     * Not supported null constructor
     *
     * @throws UnsupportedOperationException
     */
    private OverconstrainedConjugateGradientMatrixMinimizer()
    {
        super(null, null);
        throw new UnsupportedOperationException("Do not call this method.");
    }

    /**
     * Initializes a steepest-descent solver with the minimum values
     *
     * @param x0 The initial guess for x
     * @param rhs The "b" to solve to
     */
    public OverconstrainedConjugateGradientMatrixMinimizer(Vector x0,
        Vector rhs)
    {
        super(x0, rhs);
        A = null;
        residual = null;
        d = null;
        x = null;
        delta = 0;
        AtransB = null;
    }

    /**
     * Initializes a steepest-descent solver with some additional parameters
     *
     * @param x0 The initial guess for x
     * @param rhs The "b" to solve to
     * @param tolerance The minimum tolerance that this must reach before
     * stopping (unless maxIterations is exceeded).
     */
    public OverconstrainedConjugateGradientMatrixMinimizer(Vector x0,
        Vector rhs,
        double tolerance)
    {
        super(x0, rhs, tolerance);
        A = null;
        residual = null;
        d = null;
        x = null;
        delta = 0;
        AtransB = null;
    }

    /**
     * Initializes a steepest-descent solver with all user-definable parameters
     *
     * @param x0 The initial guess for x
     * @param rhs The "b" to solve to
     * @param tolerance The minimum tolerance that this must reach before
     * stopping (unless maxIterations is exceeded).
     * @param maxIterations The maximum number of iterations to make
     */
    public OverconstrainedConjugateGradientMatrixMinimizer(Vector x0,
        Vector rhs,
        double tolerance,
        int maxIterations)
    {
        super(x0, rhs, tolerance, maxIterations);
        A = null;
        residual = null;
        d = null;
        x = null;
        delta = 0;
        AtransB = null;
    }

    /**
     * Private copy constructor (for clone). Performs a shallow copy of member
     * variables.
     *
     * @param copy The other to copy into this
     */
    private OverconstrainedConjugateGradientMatrixMinimizer(
        OverconstrainedConjugateGradientMatrixMinimizer copy)
    {
        super(copy);
        this.A = copy.A;
        this.residual = copy.residual;
        this.d = copy.d;
        this.x = copy.x;
        this.delta = copy.delta;
        this.AtransB = copy.AtransB;
    }

    @Override
    final protected void initializeSolver(
        OverconstrainedMatrixVectorMultiplier function)
    {
        this.A = function;
        x = super.x0;
        AtransB = (A.transposeMult(rhs));
        residual = AtransB.minus(function.evaluate(x));
        d = residual;
        delta = residual.dotProduct(residual);
    }
    
    @Override
    final protected double iterate()
    {
        // This code is _exactly_ the same as the standard CG code because
        // evaluate does the work of A^TAx, and A^Tb was calculated in init.
        Vector q = A.evaluate(d);
        double alpha = delta / (d.dotProduct(q));
        x.plusEquals(d.scale(alpha));
        if (((iterationCounter + 1) % 50) == 0)
        {
            residual = AtransB.minus(A.evaluate(x));
        }
        else
        {
            residual = residual.minus(q.scale(alpha));
        }
        double delta_old = delta;
        delta = residual.dotProduct(residual);
        double beta = delta / delta_old;
        d = residual.plus(d.scale(beta));

        return delta;
    }

    @Override
    final protected InputOutputPair<Vector, Vector> completeSolver()
    {
        InputOutputPair<Vector, Vector> result =
            new DefaultInputOutputPair<Vector, Vector>(x0, x);
        A = null;
        residual = null;
        x = null;
        delta = 0;
        AtransB = null;

        return result;
    }

    @Override
    final public CloneableSerializable clone()
    {
        return new OverconstrainedConjugateGradientMatrixMinimizer(this);
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof OverconstrainedConjugateGradientMatrixMinimizer))
        {
            return false;
        }
        OverconstrainedConjugateGradientMatrixMinimizer other =
            (OverconstrainedConjugateGradientMatrixMinimizer) o;
        if ((A == null) && (other.A != null))
        {
            return false;
        }
        else if ((A != null) && !A.equals(other.A))
        {
            return false;
        }
        else if ((residual == null) && (other.residual != null))
        {
            return false;
        }
        else if ((residual != null) && !residual.equals(other.residual))
        {
            return false;
        }
        else if ((x == null) && (other.x != null))
        {
            return false;
        }
        else if ((x != null) && !x.equals(other.x))
        {
            return false;
        }
        else if ((d == null) && (other.d != null))
        {
            return false;
        }
        else if ((d != null) && !d.equals(other.x))
        {
            return false;
        }
        else if (delta != other.delta)
        {
            return false;
        }

        return super.equals(o);
    }

    @Override
    public int hashCode()
    {
        int hash = 1;
        hash = hash * 17 + super.hashCode();
        hash = hash * 17 + ((A == null) ? 0 : A.hashCode());
        hash = hash * 17 + ((residual == null) ? 0 : residual.hashCode());
        hash = hash * 17 + ((x == null) ? 0 : x.hashCode());
        hash = hash * 17 + ((d == null) ? 0 : d.hashCode());
        hash = hash * 17
            + Long.valueOf(Double.doubleToLongBits(delta)).hashCode();

        return hash;
    }

}
