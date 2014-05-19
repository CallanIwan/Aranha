//
//  vector3.h
//  vector
//
//  Created by Callan Kandasamy on 16-05-14.
//  Copyright (c) 2014 aranha. All rights reserved.
//

#ifndef __vector__vector3__
#define __vector__vector3__

#include <iostream>

// vector lowercase as requested by bram.
class Matrix;
class vector3
{
public:
    double x, y, z;
    vector3();
    vector3(double, double, double);
    ~vector3();
    
    double Length();
    
    static vector3 Zero();
    static vector3 One();
    static vector3 UnitX();
    static vector3 UnitY();
    static vector3 UnitZ();
    static vector3 Forward();
    static vector3 Backward();
    static vector3 Left();
    static vector3 Right();
    static vector3 Up();
    static vector3 Down();
    
    bool operator== (vector3& vec);
    vector3& operator+ (vector3 const& vec);
    vector3& operator- (vector3 const& vec);
    vector3& operator* (vector3 const& vec);
    
    vector3& operator* (double const& vec);
    vector3& operator/ (vector3 const& vec);
    vector3& operator/ (double const& vec);
    
    vector3 Transform(vector3, Matrix);
    
};

#endif /* defined(__vector__vector3__) */
