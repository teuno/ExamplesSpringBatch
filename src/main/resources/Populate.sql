SET FOREIGN_KEY_CHECKS = 0;
COMMIT;
TRUNCATE trx_;
COMMIT;
TRUNCATE orderline_;
COMMIT;
TRUNCATE order_;
COMMIT;
TRUNCATE fund_;
COMMIT;
TRUNCATE account_;
COMMIT;
TRUNCATE shares_;
COMMIT;
SET FOREIGN_KEY_CHECKS = 1;
COMMIT;
INSERT INTO fundation.fund_ (Name, Percentage) VALUES ('FondsA', 10);
INSERT INTO fundation.fund_ (Name, Percentage) VALUES ('FondsB', 40);
INSERT INTO fundation.fund_ (Name, Percentage) VALUES ('FondsC', 50);
INSERT INTO fundation.fund_ (Name, Percentage) VALUES ('FondsC', 0);
COMMIT;
INSERT INTO fundation.account_ (Accountnumber, Name) VALUES ('RN1', 'Rutger Olthuis');
INSERT INTO fundation.account_ (Accountnumber, Name) VALUES ('RN2', 'Erik van Lune');
INSERT INTO fundation.account_ (Accountnumber, Name) VALUES ('RN3', 'Teuno Hooijer');
INSERT INTO fundation.account_ (Accountnumber, Name) VALUES ('RN4', 'Zeger Tak');
INSERT INTO fundation.account_ (Accountnumber, Name) VALUES ('RN5', 'Henkie Henkersen');
COMMIT;
INSERT INTO fundation.order_ (Sum_, InputDate, AccountNumber, Status) VALUES (5000, 170505, 'RN1', 'checked');
INSERT INTO fundation.order_ (Sum_, InputDate, AccountNumber, Status) VALUES (5000, 170505, 'RN2', 'checked');
INSERT INTO fundation.order_ (Sum_, InputDate, AccountNumber, Status) VALUES (5000, 170505, 'RN3', 'PENDING');
INSERT INTO fundation.order_ (Sum_, InputDate, AccountNumber, Status) VALUES (5000, 170505, 'RN4', 'checked');
INSERT INTO fundation.order_ (Sum_, InputDate, AccountNumber, Status) VALUES (5000, 170505, 'RN5', 'checked');
COMMIT;
INSERT INTO fundation.orderline_ (OrderID, FundID, STATUS) VALUES (1, 1, 'checked');
INSERT INTO fundation.orderline_ (OrderID, FundID, STATUS) VALUES (1, 2, 'checked');
INSERT INTO fundation.orderline_ (OrderID, FundID, STATUS) VALUES (1, 3, 'checked');
INSERT INTO fundation.orderline_ (OrderID, FundID, STATUS) VALUES (2, 1, 'checked');
INSERT INTO fundation.orderline_ (OrderID, FundID, STATUS) VALUES (2, 2, 'checked');
INSERT INTO fundation.orderline_ (OrderID, FundID, STATUS) VALUES (2, 3, 'checked');
INSERT INTO fundation.orderline_ (OrderID, FundID, STATUS) VALUES (3, 1, 'pending');
INSERT INTO fundation.orderline_ (OrderID, FundID, STATUS) VALUES (3, 2, 'pending');
INSERT INTO fundation.orderline_ (OrderID, FundID, STATUS) VALUES (3, 3, 'pending');
INSERT INTO fundation.orderline_ (OrderID, FundID, STATUS) VALUES (4, 1, 'checked');
INSERT INTO fundation.orderline_ (OrderID, FundID, STATUS) VALUES (4, 2, 'checked');
INSERT INTO fundation.orderline_ (OrderID, FundID, STATUS) VALUES (4, 3, 'checked');
INSERT INTO fundation.orderline_ (OrderID, FundID, STATUS) VALUES (5, 1, 'checked');
INSERT INTO fundation.orderline_ (OrderID, FundID, STATUS) VALUES (5, 2, 'checked');
INSERT INTO fundation.orderline_ (OrderID, FundID, STATUS) VALUES (5, 3, 'checked');
COMMIT;
SET AUTOCOMMIT = 0;
COMMIT;

