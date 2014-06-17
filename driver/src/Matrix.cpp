#include "Matrix.h"

#include <iostream>
#include <math.h>
#include "Vector3.h"

Matrix::Matrix()
{
	this->M11 = 1; this->M12 = 0; this->M13 = 0; this->M14 = 0;
	this->M21 = 0; this->M22 = 1; this->M23 = 0; this->M24 = 0;
	this->M31 = 0; this->M32 = 0; this->M33 = 1; this->M34 = 0;
	this->M41 = 0; this->M42 = 0; this->M43 = 0; this->M44 = 1;
}

Matrix::Matrix(float m1, float m2, float m3, float m4,
	float m5, float m6, float m7, float m8,
	float m9, float m10, float m11, float m12,
	float m13, float m14, float m15, float m16)
{
	this->M11 = m1; this->M12 = m2; this->M13 = m3; this->M14 = m4;
	this->M21 = m5; this->M22 = m6; this->M23 = m7; this->M24 = m8;
	this->M31 = m9; this->M32 = m10; this->M33 = m11; this->M34 = m12;
	this->M41 = m13; this->M42 = m14; this->M43 = m15; this->M44 = m16;
}

Matrix::~Matrix()
{
}

void Matrix::Print()
{
	std::cout << M11 << "," << M12 << "," << M13 << "," << M14 << std::endl;
	std::cout << M21 << "," << M22 << "," << M23 << "," << M24 << std::endl;
	std::cout << M31 << "," << M32 << "," << M33 << "," << M34 << std::endl;
	std::cout << M41 << "," << M42 << "," << M43 << "," << M44 << std::endl;
}

Matrix Matrix::operator* (float scaleFactor)
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
Matrix Matrix::operator*=(float scaleFactor)
{
	*this = (*this) * scaleFactor;
	return *this;
}

Matrix Matrix::operator* (Matrix const& mat)
{
	Matrix result;
	result.M11 = this->M11 * mat.M11 + this->M12 * mat.M21 + this->M13 * mat.M31 + this->M14 * mat.M41;
	result.M12 = this->M11 * mat.M12 + this->M12 * mat.M22 + this->M13 * mat.M32 + this->M14 * mat.M42;
	result.M13 = this->M11 * mat.M13 + this->M12 * mat.M23 + this->M13 * mat.M33 + this->M14 * mat.M43;
	result.M14 = this->M11 * mat.M14 + this->M12 * mat.M24 + this->M13 * mat.M34 + this->M14 * mat.M44;
	result.M21 = this->M21 * mat.M11 + this->M22 * mat.M21 + this->M23 * mat.M31 + this->M24 * mat.M41;
	result.M22 = this->M21 * mat.M12 + this->M22 * mat.M22 + this->M23 * mat.M32 + this->M24 * mat.M42;
	result.M23 = this->M21 * mat.M13 + this->M22 * mat.M23 + this->M23 * mat.M33 + this->M24 * mat.M43;
	result.M24 = this->M21 * mat.M14 + this->M22 * mat.M24 + this->M23 * mat.M34 + this->M24 * mat.M44;
	result.M31 = this->M31 * mat.M11 + this->M32 * mat.M21 + this->M33 * mat.M31 + this->M34 * mat.M41;
	result.M32 = this->M31 * mat.M12 + this->M32 * mat.M22 + this->M33 * mat.M32 + this->M34 * mat.M42;
	result.M33 = this->M31 * mat.M13 + this->M32 * mat.M23 + this->M33 * mat.M33 + this->M34 * mat.M43;
	result.M34 = this->M31 * mat.M14 + this->M32 * mat.M24 + this->M33 * mat.M34 + this->M34 * mat.M44;
	result.M41 = this->M41 * mat.M11 + this->M42 * mat.M21 + this->M43 * mat.M31 + this->M44 * mat.M41;
	result.M42 = this->M41 * mat.M12 + this->M42 * mat.M22 + this->M43 * mat.M32 + this->M44 * mat.M42;
	result.M43 = this->M41 * mat.M13 + this->M42 * mat.M23 + this->M43 * mat.M33 + this->M44 * mat.M43;
	result.M44 = this->M41 * mat.M14 + this->M42 * mat.M24 + this->M43 * mat.M34 + this->M44 * mat.M44;
	return result;
}

Matrix Matrix::operator*=(Matrix const& mat)
{
	*this = (*this) * mat;
	return *this;
}

Matrix Matrix::CreateTranslation(float _x, float _y, float _z)
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
	result.M41 = _x;
	result.M42 = _y;
	result.M43 = _z;
	result.M44 = 1;
	return result;
}

Matrix Matrix::CreateTranslation(Vector3 vec)
{
	return Matrix::CreateTranslation(vec.x, vec.y, vec.z);
}

Matrix Matrix::CreateScale(float _x, float _y, float _z)
{
	Matrix result;
	result.M11 = _x;
	result.M12 = 0;
	result.M13 = 0;
	result.M14 = 0;
	result.M21 = 0;
	result.M22 = _y;
	result.M23 = 0;
	result.M24 = 0;
	result.M31 = 0;
	result.M32 = 0;
	result.M33 = _z;
	result.M34 = 0;
	result.M41 = 0;
	result.M42 = 0;
	result.M43 = 0;
	result.M44 = 1;
	return result;
}

Matrix Matrix::CreateScale(float scale)
{
	return Matrix::CreateScale(scale, scale, scale);
}

Matrix Matrix::CreateRotationX(float radians)
{
	float num = cos(radians);
	float num2 = sin(radians);
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
	float num = cos(radians);
	float num2 = sin(radians);
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
	float num = cos(radians);
	float num2 = sin(radians);
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

/**
Modifies the matrix with a translation matrix
*/
Matrix Matrix::Translate(float _x, float _y, float _z)
{
	M41 += _x;
	M42 += _y;
	M43 += _z;
	return *this;
}
/**
Modifies the matrix with a translation matrix
*/
Matrix Matrix::Translate(Vector3 vec)
{
	*this *= Matrix::CreateTranslation(vec);
	return *this;
}
/**
Modifies the matrix with a scaling matrix
*/
Matrix Matrix::Scale(float _x, float _y, float _z)
{
	*this *= Matrix::CreateScale(_x, _y, _z);
	return *this;
}
/**
Modifies the matrix with a rotation matrix
*/
Matrix Matrix::Rotate(float _x, float _y, float _z)
{
	Matrix rotX = Matrix::CreateRotationX(_x);
	Matrix rotY = Matrix::CreateRotationY(_y);
	Matrix rotZ = Matrix::CreateRotationZ(_z);
	*this *= rotX * rotY * rotZ;
	return *this;
}
/**
Returns the inverse version of this matrix.
*/
Matrix Matrix::Inverse()
{
	Matrix result;
	//Source: http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/fourD/index.htm
	result.M11 = M23*M34*M42 - M24*M33*M42 + M24*M32*M43 - M22*M34*M43 - M23*M32*M44 + M22*M33*M44;
	result.M12 = M14*M33*M42 - M13*M34*M42 - M14*M32*M43 + M12*M34*M43 + M13*M32*M44 - M12*M33*M44;
	result.M13 = M13*M24*M42 - M14*M23*M42 + M14*M22*M43 - M12*M24*M43 - M13*M22*M44 + M12*M23*M44;
	result.M14 = M14*M23*M32 - M13*M24*M32 - M14*M22*M33 + M12*M24*M33 + M13*M22*M34 - M12*M23*M34;
	result.M21 = M24*M33*M41 - M23*M34*M41 - M24*M31*M43 + M21*M34*M43 + M23*M31*M44 - M21*M33*M44;
	result.M22 = M13*M34*M41 - M14*M33*M41 + M14*M31*M43 - M11*M34*M43 - M13*M31*M44 + M11*M33*M44;
	result.M23 = M14*M23*M41 - M13*M24*M41 - M14*M21*M43 + M11*M24*M43 + M13*M21*M44 - M11*M23*M44;
	result.M24 = M13*M24*M31 - M14*M23*M31 + M14*M21*M33 - M11*M24*M33 - M13*M21*M34 + M11*M23*M34;
	result.M31 = M22*M34*M41 - M24*M32*M41 + M24*M31*M42 - M21*M34*M42 - M22*M31*M44 + M21*M32*M44;
	result.M32 = M14*M32*M41 - M12*M34*M41 - M14*M31*M42 + M11*M34*M42 + M12*M31*M44 - M11*M32*M44;
	result.M33 = M12*M24*M41 - M14*M22*M41 + M14*M21*M42 - M11*M24*M42 - M12*M21*M44 + M11*M22*M44;
	result.M34 = M14*M22*M31 - M12*M24*M31 - M14*M21*M32 + M11*M24*M32 + M12*M21*M34 - M11*M22*M34;
	result.M41 = M23*M32*M41 - M22*M33*M41 - M23*M31*M42 + M21*M33*M42 + M22*M31*M43 - M21*M32*M43;
	result.M42 = M12*M33*M41 - M13*M32*M41 + M13*M31*M42 - M11*M33*M42 - M12*M31*M43 + M11*M32*M43;
	result.M43 = M13*M22*M41 - M12*M23*M41 - M13*M21*M42 + M11*M23*M42 + M12*M21*M43 - M11*M22*M43;
	result.M44 = M12*M23*M31 - M13*M22*M31 + M13*M21*M32 - M11*M23*M32 - M12*M21*M33 + M11*M22*M33;
	result *= 1 / this->Determinant();
	return result;
}

float Matrix::Determinant()
{
	return
		M14*M23*M32*M41 - M13*M24*M32*M41 - M14*M22*M33*M41 + M12*M24*M33*M41 +
		M13*M22*M34*M41 - M12*M23*M34*M41 - M14*M23*M31*M42 + M13*M24*M31*M42 +
		M14*M21*M33*M42 - M11*M24*M33*M42 - M13*M21*M34*M42 + M11*M23*M34*M42 +
		M14*M22*M31*M43 - M12*M24*M31*M43 - M14*M21*M32*M43 + M11*M24*M32*M43 +
		M12*M21*M34*M43 - M11*M22*M34*M43 - M13*M22*M31*M44 + M12*M23*M31*M44 +
		M13*M21*M32*M44 - M11*M23*M32*M44 - M12*M21*M33*M44 + M11*M22*M33*M44;
}