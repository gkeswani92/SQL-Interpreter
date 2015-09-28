SELECT * FROM Sailors;
SELECT Sailors.A FROM Sailors;
SELECT S.A FROM Sailors S;
SELECT * FROM Sailors S WHERE S.A < 3;
SELECT * FROM Sailors, Reserves WHERE Sailors.A = Reserves.G;
SELECT * FROM Sailors S1, Sailors S2 WHERE S1.A < S2.A;
SELECT DISTINCT Reserves.G FROM Reserves;
SELECT * FROM Sailors ORDER BY Sailors.B;



SELECT Boats.D FROM Boats;
SELECT Boats.D, Boats.E FROM Boats WHERE Boats.D = 101;
SELECT * FROM Boats;
SELECT * FROM Boats where Boats.D = 102;
