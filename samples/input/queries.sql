Select * from Sailors,Boats,Reserves WHERE Sailors.A=Sailors.B AND Sailors.B=Boats.D;
SELECT * FROM Boats B1,Reserves R1, Reserves R2,Boats B2 WHERE B1.D=B2.D AND B2.F=R2.H AND R2.G=R1.H;
SELECT * FROM Boats B1, Reserves R, Boats B2 WHERE B1.D=R.G AND B2.E=R.H;
SELECT * FROM Reserves,Boats,Sailors WHERE Reserves.G=Sailors.A AND Sailors.B=Boats.D AND Boats.E<10 AND Reserves.H < 55 AND Sailors.C = 19;
SELECT * FROM Reserves,Sailors,Boats WHERE Boats.D=7 AND Reserves.G=70 AND Sailors.C=3;
SELECT * FROM Reserves,Sailors,Boats WHERE Reserves.G=Boats.D AND Reserves.H=Boats.F;
SELECT * FROM Reserves, Sailors, Boats WHERE Reserves.G = Boats.D AND Boats.D=Sailors.B AND Boats.E= Sailors.A;
SELECT * FROM Reserves R1,Reserves R2, Reserves R3 WHERE R1.G=R2.G AND R2.G=R3.G AND R3.H=R1.H AND R1.G=85;
SELECT * FROM Reserves, Sailors, Boats WHERE Reserves.G=Sailors.A AND Sailors.B=Boats.D AND Boats.E=Reserves.H;
select * from Sailors , Reserves, Boats WHERE Sailors.A = Boats.D AND Boats.D = Reserves.G AND Boats.E > 10 ;
select * from Sailors,Reserves,Boats WHERE Sailors.A = Boats.E AND Sailors.A = Reserves.H AND Boats.F > 100 ;
select * from Sailors,Reserves,Boats WHERE Sailors.A = Boats.E AND Sailors.A = Reserves.H AND Boats.F = 130 ;
Select * from Boats,Reserves WHERE Boats.D < 50 and Reserves.H > 25 AND Boats.D = Reserves.H;
SELECT DISTINCT S.A, R.G FROM Sailors S, Reserves R, Boats B WHERE S.B = R.G AND S.A = B.D AND R.H <> B.D AND R.H < 100 ORDER BY S.A;
SELECT S.A, R.G FROM Sailors S, Reserves R, Boats B WHERE S.B = R.G AND S.A = B.D AND R.H <> B.D AND R.H < 100 ORDER BY S.A;
SELECT * FROM Sailors S, Reserves R, Boats B WHERE S.A = R.H AND R.H <> B.D AND S.B > 50 AND S.B < 100 AND R.G < 200 AND B.D < 20;
SELECT * FROM Sailors S, Reserves R, Boats B WHERE S.A = R.H AND R.H <> B.D AND S.B > 50 AND S.B < 100;
Select * from Sailors WHERE Sailors.B > Sailors.C AND Sailors.C > Sailors.A;
select * from Sailors,Reserves WHERE Sailors.B > 4 AND Sailors.C > 10 AND Sailors.A < 50 AND Reserves.G=Reserves.H AND Sailors.A > Reserves.G;
Select * from Sailors,Reserves WHERE Sailors.B > Sailors.C AND Sailors.C > Sailors.A AND Reserves.G = Reserves.H AND Sailors.C < Reserves.G;
SELECT * FROM Sailors WHERE Sailors.A >= Sailors.B AND Sailors.B <= Sailors.C;
SELECT Reserves.G, Boats.D, Sailors.A FROM Reserves, Boats, Sailors WHERE Reserves.G < Boats.D AND Boats.D < Sailors.A AND Sailors.A > 52 and Sailors.A < 55;
SELECT Sailors.C, Reserves.H FROM Sailors, Reserves, Boats WHERE Sailors.A = Reserves.G AND Reserves.H = Boats.D AND Sailors.B < 15;
SELECT * FROM Sailors, Reserves WHERE Sailors.A = Reserves.G;
SELECT Sailors.C, Reserves.H FROM Sailors, Reserves, Boats WHERE Sailors.A = Reserves.G AND Reserves.H = Boats.D AND Sailors.B < 15;
SELECT Reserves.G, Boats.D, Sailors.A FROM Reserves, Boats, Sailors WHERE Reserves.G < Boats.D AND Boats.D < Sailors.A AND Sailors.A > 52 and Sailors.A < 55;
SELECT * FROM Sailors, Reserves, Boats WHERE Sailors.A = Reserves.G AND Reserves.H = Boats.D AND Sailors.B < 50 AND Reserves.H > 10 AND Reserves.H < 100 ORDER BY Sailors.C;
SELECT * FROM Reserves, Boats WHERE Boats.D=7 AND Reserves.G = 58;
SELECT * FROM Reserves, Boats WHERE Boats.D = 7 AND Reserves.G = 58 AND Reserves.H = Boats.F;
SELECT * FROM Reserves, Boats WHERE Reserves.H = Boats.F;
SELECT * FROM Sailors, Reserves WHERE Sailors.A < Reserves.G AND Sailors.B < 10 AND Reserves.H > 30 AND Reserves.H < 40 AND Reserves.G = 72;
SELECT * FROM Sailors, Reserves WHERE Sailors.A = Reserves.G AND Sailors.B < 10 AND Reserves.H > 30 AND Reserves.H < 40 AND Reserves.G = 72;
SELECT * FROM Sailors, Reserves WHERE Sailors.A < Reserves.G AND Sailors.B < 10 AND Reserves.H > 30 AND Reserves.H < 40 AND Reserves.G = 72 AND Sailors.C = Reserves.G;
SELECT * FROM Sailors, Reserves WHERE Sailors.A < Reserves.G AND Sailors.B < 10 AND Reserves.H > 30 AND Reserves.H < 40 AND Reserves.G = 72 ORDER BY Reserves.G;
SELECT * FROM Sailors, Reserves WHERE Sailors.A = Reserves.G AND Sailors.B < 10 AND Reserves.H > 30 AND Reserves.H < 40 AND Reserves.G = 72 AND Sailors.C = Reserves.G;
SELECT * FROM Sailors, Reserves WHERE Sailors.A < Reserves.G AND Sailors.B < 10 AND Reserves.H > 30 AND Reserves.H < 40 AND Reserves.G = 72 ORDER BY Sailors.C;
SELECT * FROM Boats B1,Reserves R1, Reserves R2,Boats B2 WHERE B1.D<B2.D AND R1.G<R2.G AND B1.E < R2.H AND B2.E<R1.H AND B2.F = B1.F AND B1.D < 10 AND B2.D < 500 AND B1.E < 100 AND R1.H < 50 AND R2.H < 100 AND R1.H > 30 AND R1.G < 100;
SELECT * FROM Sailors WHERE Sailors.A > 20;
SELECT * FROM Sailors WHERE Sailors.A > 100;
SELECT * FROM Sailors WHERE Sailors.A < 80;
SELECT * FROM Sailors WHERE Sailors.A < 0;
SELECT * FROM Sailors WHERE Sailors.A = 0;
SELECT * FROM Sailors WHERE Sailors.A = 100;
SELECT * FROM Sailors WHERE Sailors.A = 101;
SELECT * FROM Sailors WHERE Sailors.A = 40;
SELECT * FROM Sailors WHERE Sailors.A <> 0;
SELECT * FROM Sailors WHERE Sailors.A <> 100;
SELECT * FROM Sailors WHERE Sailors.A <> 20;
SELECT * FROM Sailors WHERE Sailors.A != 0;
SELECT * FROM Sailors WHERE Sailors.A != 100;
SELECT * FROM Sailors WHERE Sailors.A != 20;
SELECT * FROM Sailors WHERE Sailors.A = 0 AND Sailors.B = 45;
SELECT * FROM Sailors WHERE Sailors.A > 0 AND Sailors.A < 3;
SELECT * FROM Sailors WHERE Sailors.A > 0 AND Sailors.A < 3 and Sailors.A = 2;
SELECT * FROM Sailors WHERE Sailors.A = Sailors.B;
SELECT * FROM Sailors WHERE Sailors.A = Sailors.B AND Sailors.A > 4;
SELECT * FROM Sailors WHERE Sailors.A = Sailors.B AND Sailors.A > 6;
SELECT * FROM Sailors WHERE Sailors.A = Sailors.B AND Sailors.A < 50;
SELECT * FROM Sailors WHERE Sailors.A = Sailors.B AND Sailors.A > 6 AND Sailors.A < 50;
SELECT * FROM Sailors WHERE Sailors.A < Sailors.B;
SELECT * FROM Sailors WHERE Sailors.A < Sailors.B AND Sailors.A > 4;
SELECT * FROM Sailors WHERE Sailors.A < Sailors.B AND Sailors.A > 0;
SELECT * FROM Sailors WHERE Sailors.A < Sailors.B AND Sailors.A < 100;
SELECT * FROM Sailors WHERE Sailors.A < Sailors.B AND Sailors.A > 6 AND Sailors.A < 50;
SELECT * FROM Sailors WHERE Sailors.A < Sailors.B AND Sailors.A > 4 AND Sailors.A < 6;
SELECT * FROM Sailors WHERE Sailors.A < Sailors.B AND Sailors.A > 6 AND Sailors.B < 6;
SELECT * FROM Sailors WHERE Sailors.A < Sailors.B AND Sailors.A < 50 AND Sailors.B < 6;
SELECT * FROM Sailors WHERE Sailors.A < Sailors.B AND Sailors.A > 6 AND Sailors.A < 50 AND Sailors.B < 6;
SELECT * FROM Sailors WHERE Sailors.A < Sailors.B AND Sailors.A > 5 AND Sailors.A < 10 AND Sailors.B > 0 AND Sailors.B < 50;
SELECT * FROM Sailors WHERE Sailors.A < Sailors.B AND Sailors.A > 5 AND Sailors.A < 10 AND Sailors.B > 0 AND Sailors.B < 100;
SELECT * FROM Sailors WHERE Sailors.A < Sailors.B AND Sailors.A = 7 AND Sailors.B = 88;
SELECT * FROM Sailors WHERE Sailors.A = Sailors.B AND Sailors.B < Sailors.C AND Sailors.B = 100;
SELECT * FROM Sailors WHERE Sailors.A = Sailors.B AND Sailors.B > Sailors.C AND Sailors.B=42;
SELECT * FROM Sailors WHERE Sailors.A = Sailors.B AND Sailors.B > Sailors.C  AND Sailors.C < 50;
SELECT * FROM Sailors WHERE Sailors.A = Sailors.B AND Sailors.B > Sailors.C  AND Sailors.C < 30 AND Sailors.C > 10;
SELECT * FROM Sailors WHERE Sailors.A != Sailors.B AND Sailors.B > Sailors.C  AND Sailors.C < 30 AND Sailors.C > 10;
SELECT * FROM Sailors WHERE Sailors.A = Sailors.B AND Sailors.B = Sailors.C;
SELECT * FROM Sailors WHERE Sailors.A > Sailors.B AND Sailors.B > Sailors.C;
SELECT * FROM Sailors WHERE Sailors.A < Sailors.B AND Sailors.B < Sailors.C;
SELECT * FROM Sailors WHERE Sailors.A < Sailors.B AND Sailors.B < Sailors.C AND Sailors.A <> 44 AND Sailors.B <> 2 AND Sailors.C <> 100;
SELECT * FROM Sailors WHERE Sailors.A < Sailors.B AND Sailors.B < Sailors.C AND Sailors.C < 50;
SELECT Sailors.A FROM Sailors WHERE Sailors.A > 20;
SELECT Sailors.A, Sailors.B FROM Sailors WHERE Sailors.A > 20;
SELECT Sailors.C, Sailors.A, Sailors.B FROM Sailors WHERE Sailors.A < 80;
SELECT Sailors.A FROM Sailors WHERE Sailors.A = Sailors.B AND Sailors.B > Sailors.C  AND Sailors.C < 30 AND Sailors.C > 10;
SELECT * FROM Sailors, Reserves WHERE Sailors.A = Reserves.H AND Sailors.C < 50; 
SELECT * FROM Sailors, Reserves WHERE Sailors.A = Reserves.H AND Sailors.A < 50; 
SELECT * FROM Sailors, Reserves WHERE Sailors.A = Reserves.H AND Sailors.A < 50; 
SELECT * FROM Sailors S WHERE S.A > 20;
SELECT * FROM Sailors S WHERE S.A > 100;
SELECT * FROM Sailors S WHERE S.A < 80;
SELECT * FROM Sailors S WHERE S.A < 0;
SELECT * FROM Sailors S WHERE S.A = 0;
SELECT * FROM Sailors S WHERE S.A = 100;
SELECT * FROM Sailors S WHERE S.A <> 0;
SELECT * FROM Sailors S WHERE S.A <> 100;
SELECT * FROM Sailors S WHERE S.A <> 20;
SELECT * FROM Sailors S WHERE S.A != 0;
SELECT * FROM Sailors S WHERE S.A != 100;
SELECT * FROM Sailors S WHERE S.A != 20;
SELECT B.F, B.D FROM Boats B WHERE B.E > 450 ORDER BY B.D;
SELECT * FROM Boats B WHERE B.F>50 AND B.F>60;
SELECT S.A,S.B,B.F FROM Boats B,Sailors S WHERE B.F>60 ORDER BY S.A;
SELECT DISTINCT B.D,S.B,B.F FROM Boats B, Sailors S WHERE S.B=B.D AND B.F>50 AND S.A<1600 ORDER BY B.F;
SELECT DISTINCT B.D,S.B,B.F FROM Boats B, Sailors S WHERE B.F>50 AND S.A<1600 ORDER BY B.F;
select * from Sailors,Reserves,Boats WHERE Sailors.A = Boats.E AND Sailors.A = Reserves.H;
select * from Sailors,Reserves,Boats WHERE Sailors.A = Boats.E AND Sailors.A = Reserves.H AND Boats.F > 100 ;
SELECT * FROM Sailors WHERE Sailors.A >= Sailors.B AND Sailors.B <= Sailors.C;
SELECT DISTINCT B.E,B.D FROM Boats B WHERE B.F>50 ORDER BY B.D;
SELECT DISTINCT B.E,B.D FROM Boats B WHERE B.F>50 ORDER BY B.E;
SELECT B.F,B1.F FROM Boats B,Boats B1 WHERE B.F>50
SELECT DISTINCT B.E,B1.E FROM Boats B,Boats B1 WHERE B.F>50 AND B1.F>50
SELECT B.F,B1.F FROM Boats B,Boats B1 WHERE B.F!=50
SELECT S.A,S.B,B.F FROM Boats B,Sailors S WHERE B.F < 60 ORDER BY S.A
SELECT * FROM Boats B WHERE B.F>50 AND B.F>60
SELECT B.F,B1.F FROM Boats B,Boats B1 WHERE B.F!=50
SELECT DISTINCT B.D,S.B,B.F FROM Boats B, Sailors S WHERE S.B=B.D AND B.F>50 AND S.A<1600 ORDER BY B.F;
SELECT DISTINCT B.D,S.B,B.F FROM Boats B, Sailors S WHERE B.F>50 AND S.A<1600 ORDER BY B.F;
SELECT DISTINCT B.E,B.D FROM Boats B WHERE B.F>50 ORDER BY B.D;
SELECT DISTINCT B.E,B.D FROM Boats B WHERE B.F>50 ORDER BY B.E;
SELECT B.F,B1.F FROM Boats B,Boats B1 WHERE B.F>50
SELECT DISTINCT B.E,B1.E FROM Boats B,Boats B1 WHERE B.F>50 AND B1.F>50
SELECT B.F,B1.F FROM Boats B,Boats B1 WHERE B.F!=50
SELECT S.A,S.B,B.F FROM Boats B,Sailors S WHERE B.F>60 ORDER BY S.A
SELECT * FROM Boats B WHERE B.F>50 AND B.F>60
Select * from Sailors,Reserves WHERE Sailors.B > Sailors.C AND Sailors.C > Sailors.A AND Reserves.G = Reserves.H AND Sailors.C < Reserves.G;
Select * from Sailors,Reserves WHERE Sailors.B > Sailors.C AND Sailors.C > Sailors.A AND Reserves.G = Reserves.H ;
SELECT * FROM Reserves,Sailors,Boats WHERE Reserves.G=Sailors.A AND Sailors.B=Boats.D AND Boats.E=Reserves.H;
SELECT * FROM Reserves R1,Reserves R2, Reserves R3 WHERE R1.G=R2.G AND R2.G=R3.G AND R3.H=R1.H;
SELECT * FROM Reserves, Sailors, Boats WHERE Reserves.G = Boats.D AND Boats.D=Sailors.B AND Boats.E= Sailors.A;
SELECT * FROM Reserves,Sailors,Boats WHERE Reserves.G=Boats.D AND Reserves.H=Boats.F;
SELECT * FROM Boats, Sailors,Reserves WHERE Boats.D=Reserves.H AND Boats.E=Sailors.A AND Sailors.B=Reserves.H AND Reserves.G < 101;
SELECT * FROM Boats B1,Reserves R1, Reserves R2,Boats B2 WHERE B1.D=B2.D AND B2.F=R2.H AND R2.G=R1.H;
SELECT * FROM Boats B1,Reserves R1, Reserves R2,Boats B2 WHERE B1.D<B2.D AND R1.G<R2.G AND B1.E < R2.H AND B2.E<R1.H AND B2.F = B1.F AND B1.D < 10 AND B2.D < 500 AND B1.E < 100 AND R1.H < 50 AND R2.H < 100 AND R1.H > 30 AND R1.G < 100;
Select * from Sailors S, Boats B where S.A = B.D AND B.D > B.E AND B.E <> 10 AND S.A > 50;
Select * from Boats B1,Boats B2, Boats B3,Boats B4, Boats B5 WHERE B1.D < B2.D AND B2.D > B3.D AND B3.D < B4.D AND B5.D=B1.D AND B1.D < 10 AND B2.D < 10 AND  B3.D < 10 AND B4.D < 10 AND B5.D < 10 ;
SELECT * FROM Reserves R1,Reserves R2, Reserves R3 WHERE R1.G=R2.G AND R2.G=R3.G AND R3.H=R1.H;
SELECT * FROM Sailors R, Sailors S, Boats T WHERE R.A < 100 AND R.A = R.B AND R.B = S.C AND S.C > 50 AND R.C > 30 AND S.A = 27 AND S.A = T.F AND S.A > 50;
Select * from Sailors S, Boats B where S.A = B.D AND B.D > B.E AND B.E <> 10 AND S.A > 50;
SELECT * From Boats WHERE Boats.F=Boats.D;
SELECT * FROM Sailors S, Boats B , Reserves R WHERE S.A = S.B AND B.D=B.F AND R.H=R.G AND S.C < 50 AND S.A > 50;
Select * from Sailors S, Boats B where S.A = B.D AND B.D > B.E AND B.E <> 10 AND S.A > 50 AND S.A=S.B;
select * from Sailors , Reserves, Boats WHERE Sailors.A = Boats.D AND Boats.D = Reserves.G AND Boats.E > 9 ;
Select * from Sailors S, Boats B where S.A = B.D AND B.D > B.E AND B.E <> 10 AND S.A > 50;
Select * from Sailors S, Boats B where S.A = B.D AND B.D > B.E AND B.E <> 10 AND S.A > 50;