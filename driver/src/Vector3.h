#ifndef HEADER_GUARD_VECTOR3
#define HEADER_GUARD_VECTOR3

#include "Globals.h"

class Vector3
{
public:
	double x, y, z;
	Vector3();
	Vector3(double, double, double);
	~Vector3();

	double length();

	static Vector3 zero();
	static Vector3 one();
	static Vector3 unitX();
	static Vector3 unitY();
	static Vector3 unitZ();
	static Vector3 forward();
	static Vector3 backward();
	static Vector3 left();
	static Vector3 right();
	static Vector3 up();
	static Vector3 down();

	bool operator== (Vector3& vec);
	//
	Vector3 operator+ (Vector3 const& vec);
	Vector3 operator- (Vector3 const& vec);
	Vector3 operator* (Vector3 const& vec);
	Vector3 operator/ (Vector3 const& vec);

	Vector3 operator* (double const& vec);
	Vector3 operator/ (double const& vec);

	static Vector3 transform(Vector3, Matrix);
	void transform(Matrix);

	void print();
};
Vector3 operator*(double const&, Vector3 const&);
Vector3 operator/(double const&, Vector3 const&);

#endif