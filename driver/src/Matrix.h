#ifndef HEADER_GUARD_MATRIX
#define HEADER_GUARD_MATRIX

#include "Globals.h"

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

	Matrix operator* (Matrix const&);
	Matrix operator* (double);
	Matrix operator*=(Matrix const&);

	static Matrix CreateTranslation(double, double, double);
	static Matrix CreateTranslation(Vector3);
	static Matrix CreateScale(double);
	static Matrix CreateScale(double, double, double);

	static Matrix CreateRotationX(double);
	static Matrix CreateRotationY(double);
	static Matrix CreateRotationZ(double);

	Matrix Translate(double, double, double);
	Matrix Translate(Vector3);
	Matrix Scale(double);
	Matrix Scale(double, double, double);
	Matrix Rotate(double, double, double);

	void print();
};

#endif