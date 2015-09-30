SELECT * FROM Boats;
SELECT DISTINCT * FROM Boats;
SELECT DISTINCT Boats.id FROM Boats;
SELECT Boats.id FROM Boats;
SELECT Boats.id FROM Boats;
SELECT Boats.id,Boats.color,Boats.capacity FROM Boats;
SELECT Boats.color,Boats.id,Boats.capacity FROM Boats;
SELECT Boats.color,Boats.id,Boats.capacity FROM Boats;
SELECT Reserves.date,Reserves.rid from Reserves;
SELECT * FROM Boats WHERE 1<2;
SELECT * FROM Boats WHERE Boats.id>103;
SELECT * FROM Boats WHERE Boats.id>103 AND Boats.id<>104;
SELECT * FROM Boats WHERE Boats.id>103 AND Boats.id<107;
SELECT * FROM Boats WHERE Boats.id>103 AND Boats.color!=2;
SELECT * FROM Boats WHERE Boats.id=Boats.color;
SELECT * FROM Boats WHERE Boats.id=Boats.color AND Boats.color=2;
SELECT * FROM Boats WHERE Boats.id=Boats.color AND Boats.capacity=2 AND Boats.id!=104;
SELECT * FROM Boats WHERE Boats.id=Boats.color AND Boats.color!=2;
SELECT Boats.capacity,Boats.id FROM Boats WHERE Boats.id>103 AND Boats.id<107;
SELECT Boats.id,Boats.color FROM Boats WHERE Boats.capacity=1;
SELECT * FROM Sailors,Boats;
SELECT Sailors.id,Boats.color FROM Sailors,Boats;
SELECT DISTINCT * FROM Boats B;
SELECT DISTINCT B.id FROM Boats B;
SELECT B.id FROM Boats B;
SELECT B.id FROM Boats B;
SELECT B.id,B.color,B.capacity FROM Boats B;
SELECT B.color,B.id,B.capacity FROM Boats B;
SELECT B.color,B.id,B.capacity FROM Boats B;
SELECT R.date,R.rid from Reserves R;
SELECT * FROM Boats B WHERE 1<2;
SELECT * FROM Boats B WHERE B.id>103;
SELECT * FROM Boats B WHERE B.id>103 AND B.id<>104;
SELECT * FROM Boats B WHERE B.id>103 AND B.id<107;
SELECT * FROM Boats B WHERE B.id>103 AND B.color!=2;
SELECT * FROM Boats B WHERE B.id=B.color;
SELECT * FROM Boats B WHERE B.id=B.color AND B.color=2;
SELECT * FROM Boats B WHERE B.id=B.color AND B.capacity=2 AND B.id!=104;
SELECT * FROM Boats bo WHERE bo.id=bo.color AND bo.color!=2;
SELECT B.capacity,B.id FROM B WHERE B.id>103 AND B.id<107;
SELECT B.id,B.color FROM Boats B WHERE B.capacity=1;
SELECT * FROM Sailors,Boats;
SELECT * FROM Boats,Reserves,Sailors;
SELECT Boats.*,Reserves.*,Sailors.* FROM Boats,Reserves,Sailors;//To do 
SELECT Boats.id,Reserves.*,Sailors.* FROM Boats,Reserves,Sailors;//To do 
SELECT Boats.*,Reserves.*,Sailors.* FROM Boats,Reserves,Sailors;//To do 
SELECT * FROM Sailors,Boats,Reserves 
SELECT * FROM Boats B;
SELECT B.* FROM Boats B;//To do
SELECT s.id,b.id,r.rid FROM Boats b,Reserves r,Sailors s;
SELECT Sailors.id,Boats.color FROM Sailors,Boats