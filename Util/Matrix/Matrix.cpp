//
//  Matrix.cpp
//  vector
//
//  Created by Callan Kandasamy on 16-05-14.
//  Copyright (c) 2014 aranha. All rights reserved.
//

#include "Matrix.h"
#include <cmath>

Matrix::Matrix()
{
    this->M11 = 0; this->M12 = 0; this->M13 = 0; this->M14 = 0;
    this->M21 = 0; this->M22 = 0; this->M23 = 0; this->M24 = 0;
    this->M31 = 0; this->M32 = 0; this->M33 = 0; this->M34 = 0;
    this->M41 = 0; this->M42 = 0; this->M43 = 0; this->M44 = 0;
}

Matrix::Matrix(double m1, double m2, double m3, double m4,
               double m5, double m6, double m7, double m8,
               double m9, double m10, double m11, double m12,
               double m13, double m14, double m15, double m16)
{
    this->M11 = m1; this->M12 = m2; this->M13 = m3; this->M14 = m4;
    this->M21 = m5; this->M22 = m6; this->M23 = m7; this->M24 = m8;
    this->M31 = m9; this->M32 = m10; this->M33 = m11; this->M34 = m12;
    this->M41 = m13; this->M42 = m14; this->M43 = m15; this->M44 = m16;
}

Matrix::~Matrix()
{
    // TODO
}

void Matrix::print()
{
    std::cout << M11 << "," << M12 << "," << M13 << "," << M14 << std::endl;
    std::cout << M21 << "," << M22 << "," << M23 << "," << M24 << std::endl;
    std::cout << M31 << "," << M32 << "," << M33 << "," << M34 << std::endl;
    std::cout << M41 << "," << M42 << "," << M43 << "," << M44 << std::endl;
}

Matrix& Matrix::operator* (float scaleFactor)
{
    Matrix result;
    result.M11 = this->M11 * scaleFactor;
    result.M12 = this->M12 * scaleFactor;
    result.M13 = this->M13 * scaleFactor;
    result.M14 = this->M14 * scaleFactor;
    result.M21 = this->M21 * scaleFactor;
    result.M22 = this->M22 * scaleFactor;
    result.M23 = this->M23 * scaleFactor;
    result.M24 = this->M24 * scaleFactor;
    result.M31 = this->M31 * scaleFactor;
    result.M32 = this->M32 * scaleFactor;
    result.M33 = this->M33 * scaleFactor;
    result.M34 = this->M34 * scaleFactor;
    result.M41 = this->M41 * scaleFactor;
    result.M42 = this->M42 * scaleFactor;
    result.M43 = this->M43 * scaleFactor;
    result.M44 = this->M44 * scaleFactor;
    return result;
}

Matrix& Matrix::operator* (Matrix const& vec)
{
    Matrix result;
    result.M11 = this->M11 * vec.M11 + this->M12 * vec.M21 + this->M13 * vec.M31 + this->M14 * vec.M41;
    result.M12 = this->M11 * vec.M12 + this->M12 * vec.M22 + this->M13 * vec.M32 + this->M14 * vec.M42;
    result.M13 = this->M11 * vec.M13 + this->M12 * vec.M23 + this->M13 * vec.M33 + this->M14 * vec.M43;
    result.M14 = this->M11 * vec.M14 + this->M12 * vec.M24 + this->M13 * vec.M34 + this->M14 * vec.M44;
    result.M21 = this->M21 * vec.M11 + this->M22 * vec.M21 + this->M23 * vec.M31 + this->M24 * vec.M41;
    result.M22 = this->M21 * vec.M12 + this->M22 * vec.M22 + this->M23 * vec.M32 + this->M24 * vec.M42;
    result.M23 = this->M21 * vec.M13 + this->M22 * vec.M23 + this->M23 * vec.M33 + this->M24 * vec.M43;
    result.M24 = this->M21 * vec.M14 + this->M22 * vec.M24 + this->M23 * vec.M34 + this->M24 * vec.M44;
    result.M31 = this->M31 * vec.M11 + this->M32 * vec.M21 + this->M33 * vec.M31 + this->M34 * vec.M41;
    result.M32 = this->M31 * vec.M12 + this->M32 * vec.M22 + this->M33 * vec.M32 + this->M34 * vec.M42;
    result.M33 = this->M31 * vec.M13 + this->M32 * vec.M23 + this->M33 * vec.M33 + this->M34 * vec.M43;
    result.M34 = this->M31 * vec.M14 + this->M32 * vec.M24 + this->M33 * vec.M34 + this->M34 * vec.M44;
    result.M41 = this->M41 * vec.M11 + this->M42 * vec.M21 + this->M43 * vec.M31 + this->M44 * vec.M41;
    result.M42 = this->M41 * vec.M12 + this->M42 * vec.M22 + this->M43 * vec.M32 + this->M44 * vec.M42;
    result.M43 = this->M41 * vec.M13 + this->M42 * vec.M23 + this->M43 * vec.M33 + this->M44 * vec.M43;
    result.M44 = this->M41 * vec.M14 + this->M42 * vec.M24 + this->M43 * vec.M34 + this->M44 * vec.M44;
    return result;
}

Matrix Matrix::CreateTranslation(vector3 position)
{
    Matrix result;
    result.M11 = 1;
    result.M12 = 0;
    result.M13 = 0;
    result.M14 = 0;
    result.M21 = 0;
    result.M22 = 1;
    result.M23 = 0;
    result.M24 = 0;
    result.M31 = 0;
    result.M32 = 0;
    result.M33 = 1;
    result.M34 = 0;
    result.M41 = position.x;
    result.M42 = position.y;
    result.M43 = position.z;
    result.M44 = 1;
    return result;
}

Matrix Matrix::CreateScale(float scale)
{
    Matrix result;
    result.M11 = scale;
    result.M12 = 0;
    result.M13 = 0;
    result.M14 = 0;
    result.M21 = 0;
    result.M22 = scale;
    result.M23 = 0;
    result.M24 = 0;
    result.M31 = 0;
    result.M32 = 0;
    result.M33 = scale;
    result.M34 = 0;
    result.M41 = 0;
    result.M42 = 0;
    result.M43 = 0;
    result.M44 = 1;
    return result;
}

Matrix Matrix::CreateRotationX(float radians)
{
    float num = (float)cos((double)radians);
    float num2 = (float)sin((double)radians);
    Matrix result;
    result.M11 = 1;
    result.M12 = 0;
    result.M13 = 0;
    result.M14 = 0;
    result.M21 = 0;
    result.M22 = num;
    result.M23 = num2;
    result.M24 = 0;
    result.M31 = 0;
    result.M32 = -num2;
    result.M33 = num;
    result.M34 = 0;
    result.M41 = 0;
    result.M42 = 0;
    result.M43 = 0;
    result.M44 = 1;
    return result;
}

Matrix Matrix::CreateRotationY(float radians)
{
    float num = (float)cos((double)radians);
    float num2 = (float)sin((double)radians);
    Matrix result;
    result.M11 = num;
    result.M12 = 0;
    result.M13 = -num2;
    result.M14 = 0;
    result.M21 = 0;
    result.M22 = 1;
    result.M23 = 0;
    result.M24 = 0;
    result.M31 = num2;
    result.M32 = 0;
    result.M33 = num;
    result.M34 = 0;
    result.M41 = 0;
    result.M42 = 0;
    result.M43 = 0;
    result.M44 = 1;
    return result;
}

Matrix Matrix::CreateRotationZ(float radians)
{
    float num = (float)cos((double)radians);
    float num2 = (float)sin((double)radians);
    Matrix result;
    result.M11 = num;
    result.M12 = num2;
    result.M13 = 0;
    result.M14 = 0;
    result.M21 = -num2;
    result.M22 = num;
    result.M23 = 0;
    result.M24 = 0;
    result.M31 = 0;
    result.M32 = 0;
    result.M33 = 1;
    result.M34 = 0;
    result.M41 = 0;
    result.M42 = 0;
    result.M43 = 0;
    result.M44 = 1;
    return result;
}
/*
vector3& Matrix::operator* (vector3 const& vec)
{
    // TODO
}*/