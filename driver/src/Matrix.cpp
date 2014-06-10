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

Matrix Matrix::operator* (double scaleFactor)
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
	M11 = M11 * mat.M11 + M12 * mat.M21 + M13 * mat.M31 + M14 * mat.M41;
	M12 = M11 * mat.M12 + M12 * mat.M22 + M13 * mat.M32 + M14 * mat.M42;
	M13 = M11 * mat.M13 + M12 * mat.M23 + M13 * mat.M33 + M14 * mat.M43;
	M14 = M11 * mat.M14 + M12 * mat.M24 + M13 * mat.M34 + M14 * mat.M44;
	M21 = M21 * mat.M11 + M22 * mat.M21 + M23 * mat.M31 + M24 * mat.M41;
	M22 = M21 * mat.M12 + M22 * mat.M22 + M23 * mat.M32 + M24 * mat.M42;
	M23 = M21 * mat.M13 + M22 * mat.M23 + M23 * mat.M33 + M24 * mat.M43;
	M24 = M21 * mat.M14 + M22 * mat.M24 + M23 * mat.M34 + M24 * mat.M44;
	M31 = M31 * mat.M11 + M32 * mat.M21 + M33 * mat.M31 + M34 * mat.M41;
	M32 = M31 * mat.M12 + M32 * mat.M22 + M33 * mat.M32 + M34 * mat.M42;
	M33 = M31 * mat.M13 + M32 * mat.M23 + M33 * mat.M33 + M34 * mat.M43;
	M34 = M31 * mat.M14 + M32 * mat.M24 + M33 * mat.M34 + M34 * mat.M44;
	M41 = M41 * mat.M11 + M42 * mat.M21 + M43 * mat.M31 + M44 * mat.M41;
	M42 = M41 * mat.M12 + M42 * mat.M22 + M43 * mat.M32 + M44 * mat.M42;
	M43 = M41 * mat.M13 + M42 * mat.M23 + M43 * mat.M33 + M44 * mat.M43;
	M44 = M41 * mat.M14 + M42 * mat.M24 + M43 * mat.M34 + M44 * mat.M44;
	return *this;
}


Matrix Matrix::CreateTranslation(double _x, double _y, double _z)
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

Matrix Matrix::CreateScale(double _x, double _y, double _z)
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

Matrix Matrix::CreateScale(double scale)
{
	return Matrix::CreateScale(scale, scale, scale);
}

Matrix Matrix::CreateRotationX(double radians)
{
	double num = cos(radians);
	double num2 = sin(radians);
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

Matrix Matrix::CreateRotationY(double radians)
{
	double num = cos(radians);
	double num2 = sin(radians);
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

Matrix Matrix::CreateRotationZ(double radians)
{
	double num = cos(radians);
	double num2 = sin(radians);
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

Matrix Matrix::Translate(double _x, double _y, double _z)
{
	M41 += _x;
	M42 += _y;
	M43 += _z;
	return *this;
}

Matrix Matrix::Translate(Vector3 vec)
{
	return Translate(vec.x, vec.y, vec.z);
}

Matrix Matrix::Scale(double _x, double _y, double _z)
{
	*this *= Matrix::CreateScale(_x, _y, _z);
	return *this;
}

Matrix Matrix::Scale(double scale)
{
	return Scale(scale, scale, scale);
}

Matrix Matrix::Rotate(double _x, double _y, double _z)
{
	Matrix rotX = Matrix::CreateRotationX(_x);
	Matrix rotY = Matrix::CreateRotationY(_y);
	Matrix rotZ = Matrix::CreateRotationZ(_z);
	*this *= rotX * rotY * rotZ;
	return *this;
}
