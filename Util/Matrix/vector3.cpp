//
//  vector3.cpp
//  vector
//
//  Created by Callan Kandasamy on 16-05-14.
//  Copyright (c) 2014 aranha. All rights reserved.
//

#include "vector3.h"
#include "Matrix.h"

vector3::vector3()
{
    x = 0;
    y = 0;
    z = 0;
}

vector3::vector3(double _x, double _y, double _z)
{
    x = _x;
    y = _y;
    z = _z;
}

vector3::~vector3()
{
    
}

double Length();

vector3 vector3::Zero()
{
    return *new vector3();
}

 vector3 vector3::One()
{
    return *new vector3(1, 1, 1);
}

 vector3 vector3::UnitX()
{
    return *new vector3(1, 0, 0);
}

 vector3 vector3::UnitY()
{
    return *new vector3(0, 1, 0);
}

 vector3 vector3::UnitZ()
{
    return *new vector3(0, 0, 1);
}

 vector3 vector3::Forward()
{
    return *new vector3(0, 0, -1);
}

 vector3 vector3::Backward()
{
    return *new vector3(0, 0, 1);
}

 vector3 vector3::Left()
{
    return *new vector3(-1, 0, 0);
}

vector3 vector3::Right()
{
    return *new vector3(1, 0, 0);
}

 vector3 vector3::Up()
{
    return *new vector3(0, 1, 0);
}

vector3 vector3::Down()
{
    return *new vector3(0, -1, 0);
}

bool vector3::operator== (vector3& vec)
{
    return (x == vec.x && y == vec.y && z == vec.z);
}


vector3& vector3::operator+ (vector3 const& vec)
{
    vector3 result;
    result.x = this->x + vec.x;
    result.y = this->y + vec.y;
    result.z = this->z + vec.z;
    return result;
}

vector3& vector3::operator- (vector3 const& vec)
{
    vector3 result = *this;
    result.x -= - vec.x;
    result.y -= this->y - vec.y;
    result.z -= this->z - vec.z;
    return result;

}

vector3& vector3::operator* (vector3 const& vec)
{
    vector3 result;
    result.x = this->x * vec.x;
    result.y = this->y * vec.y;
    result.z = this->z * vec.z;
    return result;
}

vector3&  vector3::operator* (double const& vec)
{
    vector3 result;
    result.x *= vec;
    result.y *=vec;
    result.z *= vec;
    return result;
}

vector3& vector3::operator/ (vector3 const& vec)
{
    vector3 result;
    result.x = this->x / vec.x;
    result.y = this->y / vec.y;
    result.z = this->z / vec.z;
    return result;

}

vector3& vector3::operator/ (double const& vec)
{
    vector3 result;
    result.x /= vec;
    result.y /= vec;
    result.z /= vec;
    return result;
}

vector3 vector3::Transform(vector3 position, Matrix matrix)
{
    float x = position.x * matrix.M11 + position.y * matrix.M21 + position.z * matrix.M31 + matrix.M41;
    float y = position.x * matrix.M12 + position.y * matrix.M22 + position.z * matrix.M32 + matrix.M42;
    float z = position.x * matrix.M13 + position.y * matrix.M23 + position.z * matrix.M33 + matrix.M43;
    vector3 result;
    result.x = x;
    result.y = y;
    result.z = z;
    return result;
}

