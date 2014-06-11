#ifndef HEADER_GUARD_MATRIX
#define HEADER_GUARD_MATRIX

#include "Globals.h"

class Matrix
{
public:
	float  M11, M12, M13, M14,
		M21, M22, M23, M24,
		M31, M32, M33, M34,
		M41, M42, M43, M44;
	Matrix();
	Matrix(float, float, float, float,
		float, float, float, float,
		float, float, float, float,
		float, float, float, float);
	~Matrix();

	Matrix operator* (Matrix const&);
	Matrix operator*=(Matrix const&);
	Matrix operator* (float);
	Matrix operator*=(float);

	static Matrix CreateTranslation(float, float, float);
	static Matrix CreateTranslation(Vector3);
	static Matrix CreateScale(float);
	static Matrix CreateScale(float, float, float);

	static Matrix CreateRotationX(float);
	static Matrix CreateRotationY(float);
	static Matrix CreateRotationZ(float);

	Matrix Translate(float, float, float);
	Matrix Translate(Vector3);
	Matrix Scale(float);
	Matrix Scale(float, float, float);
	Matrix Rotate(float, float, float);

	Matrix Inverse();
	float Determinant();
	void Print();
};

#endif