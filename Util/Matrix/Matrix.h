//
//  Matrix.h
//  vector
//
//  Created by Callan Kandasamy on 16-05-14.
//  Copyright (c) 2014 aranha. All rights reserved.
//

#ifndef __vector__Matrix__
#define __vector__Matrix__

#include "vector3.h"

class Matrix
{
public:
    double  M11, M12, M13, M14,
            M21, M22, M23, M24,
            M31, M32, M33, M34,
            M41, M42, M43, M44;
    Matrix();
    Matrix(double, double, double, double,
           double, double, double, double,
           double, double, double, double,
           double, double, double, double);
    ~Matrix();
    
    Matrix& operator* (Matrix const& vec);
    Matrix& operator* (float);
    vector3& operator* (vector3 const& vec);
    
    Matrix CreateTranslation(vector3);
    Matrix CreateScale(float);
    
    Matrix CreateRotationX(float);
    Matrix CreateRotationY(float);
    Matrix CreateRotationZ(float);
    void print();


};

class TransformationMatrix: public Matrix
{
    
};

class TranslationMatrix: public Matrix
{
    
};

#endif /* defined(__vector__Matrix__) */
