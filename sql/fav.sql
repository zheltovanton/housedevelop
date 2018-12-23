CREATE TABLE fav
( fav_id INT(11) NOT NULL AUTO_INCREMENT,
  fav_user_id VARCHAR(30) NOT NULL,
  fav_house_id int,
  fav_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fav_pk PRIMARY KEY (fav_id)
);

SELECT `fav_house_id` , shothdesc, user_uid, developer, developer_desc, address, city, region, country, housedetails, created_at, (

SELECT name
FROM users
WHERE unique_id = fav_user_id
) AS username
FROM `fav`
INNER JOIN house ON id = fav_house_id
WHERE `fav_user_id` = '54feeee0a125a6.83910404'
ORDER BY `fav_date` DESC 