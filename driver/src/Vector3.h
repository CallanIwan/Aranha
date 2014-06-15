#ifndef HEADER_GUARD_VECTOR3
#define HEADER_GUARD_VECTOR3

#include "Globals.h"

class Vector3
{
public:
	float x, y, z;
	Vector3();
	Vector3(float, float, float);
	~Vector3();

	float Length();

	static Vector3 Zero();
	static Vector3 One();
	static Vector3 UnitX();
	static Vector3 UnitY();
	static Vector3 UnitZ();
	static Vector3 Forward();
	static Vector3 Backward();
	static Vector3 Left();
	static Vector3 Right();
	static Vector3 Up();
	static Vector3 Down();

	bool operator== (Vector3& vec);
	//
	Vector3 operator+ (Vector3 const& vec);
	Vector3 operator- (Vector3 const& vec);
	Vector3 operator* (Vector3 const& vec);
	Vector3 operator/ (Vector3 const& vec);
	Vector3 operator+=(Vector3 const& vec);
	Vector3 operator-=(Vector3 const& vec);
	Vector3 operator*=(Vector3 const& vec);
	Vector3 operator/=(Vector3 const& vec);

	Vector3 operator* (float const& vec);
	Vector3 operator/ (float const& vec);
	Vector3 operator*=(float const& vec);
	Vector3 operator/=(float const& vec);

	static Vector3 Transform(Vector3, Matrix);
	static Vector3 Clamp(Vector3);
	void Transform(Matrix);

	void Print();
};
Vector3 operator*(float const&, Vector3 const&);

#endif