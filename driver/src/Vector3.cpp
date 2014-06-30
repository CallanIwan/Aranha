#include "Vector3.h"

#include <stdio.h>
#include <math.h>
#include "Matrix.h"

Vector3::Vector3()
{
	x = 0;
	y = 0;
	z = 0;
}

Vector3::Vector3(float _x, float _y, float _z)
{
	x = _x;
	y = _y;
	z = _z;
}

Vector3::~Vector3()
{
}

float Vector3::Length()
{
	return sqrt((x * x) + (y * y) + (z * z));
}

Vector3 Vector3::Zero()
{
	return Vector3(0, 0, 0);
}

Vector3 Vector3::One()
{
	return Vector3(1, 1, 1);
}

Vector3 Vector3::UnitX()
{
	return Vector3(1, 0, 0);
}

Vector3 Vector3::UnitY()
{
	return Vector3(0, 1, 0);
}

Vector3 Vector3::UnitZ()
{
	return Vector3(0, 0, 1);
}

Vector3 Vector3::Forward()
{
	//Forward: positive X
	return Vector3::UnitX();
}

Vector3 Vector3::Backward()
{
	//Backward: negative X
	return Vector3::UnitX() * -1;
}

Vector3 Vector3::Left()
{
	//Left: -90 degree rotation on forward vector
	return Vector3(0, 0, 1);
}

Vector3 Vector3::Right()
{
	//Right: 90 degree rotation on forward vector
	return Vector3(0, 0, -1);
}

Vector3 Vector3::Up()
{
	//Up: postive Y
	return Vector3::UnitY();
}

Vector3 Vector3::Down()
{
	//Down: negative Y
	return Vector3::UnitY() * -1;
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

Vector3 Vector3::operator* (float const& factor)
{
	Vector3 result;
	result.x = this->x * factor;
	result.y = this->y * factor;
	result.z = this->z * factor;
	return result;
}
Vector3 operator*(float const& factor, Vector3 const& vec)
{
	Vector3 result;
	result.x = vec.x * factor;
	result.y = vec.y * factor;
	result.z = vec.z * factor;
	return result;
}

Vector3 Vector3::operator/(float const& factor)
{
	Vector3 result;
	result.x = this->x / factor;
	result.y = this->y / factor;
	result.z = this->z / factor;
	return result;
}
Vector3 operator/(float const& factor, Vector3 const& vec)
{
	Vector3 result;
	result.x = vec.x / factor;
	result.y = vec.y / factor;
	result.z = vec.z / factor;
	return result;
}

Vector3 Vector3::operator+=(Vector3 const& vec)
{
	this->x += vec.x;
	this->y += vec.y;
	this->z += vec.z;
	return *this;
}

Vector3 Vector3::operator-=(Vector3 const& vec)
{
	this->x -= vec.x;
	this->y -= vec.y;
	this->z -= vec.z;
	return *this;
}

Vector3 Vector3::operator*=(Vector3 const& vec)
{
	this->x *= vec.x;
	this->y *= vec.y;
	this->z *= vec.z;
	return *this;
}

Vector3 Vector3::operator/=(Vector3 const& vec)
{
	this->x /= vec.x;
	this->y /= vec.y;
	this->z /= vec.z;
	return *this;
}

Vector3 Vector3::operator*=(float const& factor)
{
	this->x *= factor;
	this->y *= factor;
	this->z *= factor;
	return *this;
}

Vector3 Vector3::operator/=(float const& factor)
{
	this->x /= factor;
	this->y /= factor;
	this->z /= factor;
	return *this;
}

Vector3 Vector3::Transform(Vector3 position, Matrix matrix)
{
	float x = position.x * matrix.M11 + position.y * matrix.M21 + position.z * matrix.M31 + matrix.M41;
	float y = position.x * matrix.M12 + position.y * matrix.M22 + position.z * matrix.M32 + matrix.M42;
	float z = position.x * matrix.M13 + position.y * matrix.M23 + position.z * matrix.M33 + matrix.M43;
	Vector3 result;
	result.x = x;
	result.y = y;
	result.z = z;
	return result;
}

void Vector3::Transform(Matrix mat)
{
	*this = Vector3::Transform(*this, mat);
}
Vector3 Vector3::Clamp(Vector3 source)
{
	Vector3 result(source);
	float max = fmaxf(fmaxf(result.x, result.y), result.z);
	result *= 1 / max;
	return result;
}

void Vector3::Print()
{
	printf("Vector3: {%f, %f, %f}\n", x, y, z);
	//printf("Vector3: {%4.2f, %4.2f, %4.2f}\n", x, y, z);
	//std::cout << "Vector3: {" << x << ',' << y << ',' << z << "}\n";
}