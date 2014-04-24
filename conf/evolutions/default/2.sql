-- Insert some OHs

# --- !Ups
INSERT INTO OH (name, size) VALUES ('Soju', '7 shots of speeding'), ('Beer', 'a pint of buzz'), ('Makgeolli', 'a gulp of culture'), ('Bourbon', 'a shot of manliness'), ('Wine', 'a glass of class'), ('Vodka', 'rocket shot'), ('Cocktail', 'casual throwback');
INSERT INTO BIZ (name, address) VALUES ('Jinsu', '11127 La Maida St'), ('Gam', 'Somewhere in KTown'), ('Hodu', 'In Porto Ranch');
INSERT INTO DRINK_PRICE (biz_id, oh_id, price) values (1, 1, 10.05), (1, 3, 8), (2, 1, 11), (3, 2, 9);

# --- !Downs

