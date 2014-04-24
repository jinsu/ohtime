
# --- !Ups

-- Utility function
CREATE OR REPLACE FUNCTION set_updated_at()
    RETURNS TRIGGER AS $$
    BEGIN
        NEW.updated_at = now();;
        RETURN NEW;;
    END;;
    $$ LANGUAGE 'plpgsql';

-- Creates schema for all tables.
CREATE TABLE Biz (
  id BIGSERIAL,
  name VARCHAR(255) NOT NULL,
  created_at TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
  coord POINT,
  address VARCHAR(255),
  PRIMARY KEY (id)
);

CREATE TABLE OH (
  id BIGSERIAL,
  name VARCHAR(255) NOT NULL,
  size VARCHAR(20) DEFAULT '',
  created_at TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

CREATE TABLE Drink_Price (
  id BIGSERIAL,
  price NUMERIC(9,2) NOT NULL,
  biz_id BIGSERIAL,
  oh_id BIGSERIAL,
  created_at TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
  effective_at TIMESTAMP(0) DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT oh_id_integrity
    FOREIGN KEY (oh_id)
    REFERENCES OH(id)
    ON DELETE CASCADE,
  CONSTRAINT biz_id_integrity
    FOREIGN KEY (biz_id)
    REFERENCES Biz(id)
    ON DELETE CASCADE
);

CREATE TRIGGER renew_price_updated_at BEFORE UPDATE ON Drink_price FOR EACH ROW EXECUTE PROCEDURE set_updated_at();
CREATE TRIGGER renew_oh_updated_at BEFORE UPDATE ON OH FOR EACH ROW EXECUTE PROCEDURE set_updated_at();
CREATE TRIGGER renew_biz_updated_at BEFORE UPDATE ON Biz FOR EACH ROW EXECUTE PROCEDURE set_updated_at();

# --- !Downs
DROP TABLE Drink_Price;
DROP TABLE OH;
DROP TABLE Biz;
