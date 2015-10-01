SELECT * FROM Sailors;
SELECT Sailors.A FROM Sailors;
SELECT S.A FROM Sailors S;
SELECT * FROM Sailors S WHERE S.A < 3;
SELECT * FROM Sailors, Reserves WHERE Sailors.A = Reserves.G;
SELECT * FROM Sailors S1, Sailors S2 WHERE S1.A < S2.A;
SELECT DISTINCT Reserves.G FROM Reserves;
SELECT * FROM Sailors ORDER BY Sailors.B;

SELECT * FROM Sailors;

SELECT * FROM Sailors WHERE Sailors.A = 1;
SELECT * FROM Sailors WHERE Sailors.A < 10;
SELECT * FROM Sailors WHERE Sailors.A <= 3;
SELECT * FROM Sailors WHERE Sailors.A >= 3;
SELECT * FROM Sailors WHERE Sailors.A > 3;
SELECT * FROM Sailors WHERE Sailors.A <> 3;
SELECT * FROM Sailors WHERE Sailors.A != 3;
SELECT * FROM Sailors WHERE Sailors.A != 3 AND Sailors.B > 100;

SELECT * FROM Sailors WHERE 1 = Sailors.A;
SELECT * FROM Sailors WHERE 10 > Sailors.A;
SELECT * FROM Sailors WHERE 3 >= Sailors.A;

SELECT * FROM Sailors WHERE 3 <= Sailors.A;
SELECT * FROM Sailors WHERE 3 < Sailors.A;
SELECT * FROM Sailors WHERE 3 <> Sailors.A;
SELECT * FROM Sailors WHERE 3 != Sailors.A;
SELECT * FROM Sailors WHERE 3 != Sailors.A AND 100 < Sailors.B;

SELECT * FROM Sailors WHERE 1=0;
SELECT * FROM Sailors WHERE 1=1;
SELECT Sailors.B FROM Sailors WHERE 3 <> Sailors.A;
SELECT Sailors.A, Sailors.B FROM Sailors WHERE 1 = Sailors.A;

SELECT * FROM Boats, Sailors ORDER BY Boats.D;
SELECT * FROM Boats, Sailors WHERE Boats.E = Sailors.A;
SELECT * FROM Boats, Sailors WHERE 1 = Sailors.A;
SELECT * FROM Boats, Sailors WHERE Sailors.A = 1;
SELECT * FROM Boats, Sailors WHERE Boats.E = Sailors.A AND Boats.D < Sailors.B;
SELECT * FROM Sailors, Reserves WHERE Sailors.A = Reserves.G;
SELECT * FROM Boats B1, Boats B2;
SELECT * FROM Boats B1, Boats B2 WHERE B1.D = B2.D;
SELECT * FROM Boats B1, Boats B2 WHERE B1.D = B2.D AND B1.D = 101;
SELECT * FROM Boats, Sailors WHERE Sailors.A < 2 AND Boats.E = Sailors.A AND Boats.D < Sailors.B;

SELECT B1.D, B2.E FROM Boats B1, Boats B2 WHERE B1.D = B2.D AND B1.D = 101;

SELECT DISTINCT Reserves.G FROM Reserves;
SELECT DISTINCT * FROM Boats;

SELECT * FROM Boats ORDER BY Boats.E;