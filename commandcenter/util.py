import math


def pair(tup):
    return (tup[0], tup[1]) if len(tup) > 2 else tup


def dotproduct(v1, v2):
  return sum((a*b) for a, b in zip(v1, v2))

def length(v):
  return math.sqrt(dotproduct(v, v))

def angle(v1, v2):
  return math.acos(dotproduct(v1, v2) / (length(v1) * length(v2)))

def angleToPoint(vec):
    rotation2 = angle((0.001, 0.001), vec)
