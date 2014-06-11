#include "Vector3.h"

#include <iostream>
#include <math.h>
#include "Matrix.h"

Vector3::Vector3()
{
	x = 0;
	y = 0;
	z = 0;
}

Vector3::Vector3(double _x, double _y, double _z)
{
	x = _x;
	y = _y;
	z = _z;
}

Vector3::~Vector3()
{
	
}

double Vector3::length()
{
	return sqrt((x * x) + (y * y) + (z * z));
}

Vector3 Vector3::zero()
{
	return Vector3();
}

 Vector3 Vector3::one()
{
	return Vector3(1, 1, 1);
}

 Vector3 Vector3::unitX()
{
	return Vector3(1, 0, 0);
}

 Vector3 Vector3::unitY()
{
	return Vector3(0, 1, 0);
}

 Vector3 Vector3::unitZ()
{
	return Vector3(0, 0, 1);
}

 Vector3 Vector3::forward()
{
	return Vector3(0, 0, -1);
}

 Vector3 Vector3::backward()
{
	return Vector3(0, 0, 1);
}

 Vector3 Vector3::left()
{
	return Vector3(-1, 0, 0);
}

Vector3 Vector3::right()
{
	return Vector3(1, 0, 0);
}

 Vector3 Vector3::up()
{
	return Vector3(0, 1, 0);
}

Vector3 Vector3::down()
{
	return Vector3(0, -1, 0);
}

bool Vector3::operator== (Vector3& vec)
{
	return (x == vec.x && y == vec.y && z == vec.z);
}

Vector3 Vector3::operator+ (Vector3 const& vec)
{
	Vector3 result;
	result.x = this->x + vec.x;
	result.y = this->y + vec.y;
	result.z = this->z + vec.z;
	return result;
}

Vector3 Vector3::operator- (Vector3 const& vec)
{
	Vector3 result;
	result.x = this->x - vec.x;
	result.y = this->y - vec.y;
	result.z = this->z - vec.z;
	return result;
}

Vector3 Vector3::operator* (Vector3 const& vec)
{
	Vector3 result;
	result.x = this->x * vec.x;
	result.y = this->y * vec.y;
	result.z = this->z * vec.z;
	return result;
}

Vector3 Vector3::operator/ (Vector3 const& vec)
{
	Vector3 result;
	result.x = this->x / vec.x;
	result.y = this->y / vec.y;
	result.z = this->z / vec.z;
	return result;
}

Vector3 Vector3::operator* (double const& factor)
{
	Vector3 result;
	result.x = this->x * factor;
	result.y = this->y * factor;
	result.z = this->z * factor;
	return result;
}
Vector3 operator*(double const& factor ,Vector3 const& vec)
{
	Vector3 result;
	result.x = vec.x * factor;
	result.y = vec.y * factor;
	result.z = vec.z * factor;
	return result;
}

Vector3 Vector3::operator/(double const& factor)
{
	Vector3 result;
	result.x = this->x / factor;
	result.y = this->y / factor;
	result.z = this->z / factor;
	return result;
}
Vector3 operator/(double const& factor ,Vector3 const& vec)
{
	Vector3 result;
	result.x = vec.x / factor;
	result.y = vec.y / factor;
	result.z = vec.z / factor;
	return result;
}

Vector3 Vector3::transform(Vector3 position, Matrix matrix)
{
	double x = position.x * matrix.M11 + position.y * matrix.M21 + position.z * matrix.M31 + matrix.M41;
	double y = position.x * matrix.M12 + position.y * matrix.M22 + position.z * matrix.M32 + matrix.M42;
	double z = position.x * matrix.M13 + position.y * matrix.M23 + position.z * matrix.M33 + matrix.M43;
	Vector3 result;
	result.x = x;
	result.y = y;
	result.z = z;
	return result;
}

void Vector3::transform(Matrix mat)
{
	*this = Vector3::transform(*this,mat);
}

void Vector3::print()
{
	std::cout << "Vector3: {" << x << ',' << y << ',' << z << "}\n";
}