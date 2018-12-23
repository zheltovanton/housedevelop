-- phpMyAdmin SQL Dump
-- version 4.0.10.7
-- http://www.phpmyadmin.net
--
-- Хост: localhost
-- Время создания: Апр 15 2015 г., 09:24
-- Версия сервера: 5.6.23
-- Версия PHP: 5.4.23

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- База данных: `housedevelop`
--

-- --------------------------------------------------------

--
-- Структура таблицы `comments`
--

DROP TABLE IF EXISTS `comments`;
CREATE TABLE IF NOT EXISTS `comments` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `filename` varchar(255) DEFAULT NULL,
  `comment` varchar(10000) DEFAULT NULL,
  `marker_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `user_id` varchar(30) NOT NULL,
  `deleted` enum('0','1') NOT NULL DEFAULT '0',
  `date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_page_id` (`user_id`),
  KEY `idx_position` (`marker_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=148 ;

--
-- Дамп данных таблицы `comments`
--

INSERT INTO `comments` (`id`, `filename`, `comment`, `marker_id`, `user_id`, `deleted`, `date`) VALUES
(145, '6vR3V7owgX20150412_130049.jpg', '%D0%BA%D0%BE%D0%BD%D1%82%D0%B0%D0%BA%D1%82%D1%8B+%D0%B8+%D0%B8%D0%BD%D1%84%D0%BE', 73, '54feeee0a125a6.83910404', '0', '2015-04-15 11:43:00'),
(146, 'wkc7vkmb2020150412_130105.jpg', '%D1%84%D0%BE%D1%82%D0%BE', 73, '54feeee0a125a6.83910404', '0', '2015-04-15 12:10:14'),
(147, 'ounzk0w8Tx20150412_125522.jpg', '%D0%BA%D0%BE%D0%BD%D1%82%D0%B0%D0%BA%D1%82%D1%8B', 74, '54feeee0a125a6.83910404', '0', '2015-04-15 12:58:44');

-- --------------------------------------------------------

--
-- Структура таблицы `fav`
--

DROP TABLE IF EXISTS `fav`;
CREATE TABLE IF NOT EXISTS `fav` (
  `fav_id` int(11) NOT NULL AUTO_INCREMENT,
  `fav_user_id` varchar(30) NOT NULL,
  `fav_house_id` int(11) DEFAULT NULL,
  `fav_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`fav_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=53 ;

--
-- Дамп данных таблицы `fav`
--

INSERT INTO `fav` (`fav_id`, `fav_user_id`, `fav_house_id`, `fav_date`) VALUES
(51, '54feeee0a125a6.83910404', 73, '2015-04-15 12:29:35'),
(52, '54feeee0a125a6.83910404', 74, '2015-04-15 12:57:01');

-- --------------------------------------------------------

--
-- Структура таблицы `house`
--

DROP TABLE IF EXISTS `house`;
CREATE TABLE IF NOT EXISTS `house` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(60)  DEFAULT NULL,
  `lat` float(10,6) NOT NULL,
  `lng` float(10,6) NOT NULL,
  `shothdesc` varchar(250)  DEFAULT NULL,
  `user_uid` varchar(23)   NOT NULL,
  `developer` varchar(23)  NOT NULL,
  `developer_desc` varchar(500)  NOT NULL,
  `address` varchar(250)  DEFAULT NULL,
  `city` varchar(50)  NOT NULL,
  `region` varchar(100)  NOT NULL,
  `country` varchar(50)  NOT NULL,
  `housedetails` varchar(1000)  NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `develop_finish_at` date DEFAULT NULL,
  `develop_begin_at` date DEFAULT NULL,
  `stage` varchar(50) NOT NULL DEFAULT 'permission',
  PRIMARY KEY (`id`),
  KEY `id` (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=utf8 AUTO_INCREMENT=75 ;

--
-- Дамп данных таблицы `house`
--

INSERT INTO `house` (`id`, `name`, `lat`, `lng`, `shothdesc`, `user_uid`, `developer`, `developer_desc`, `address`, `city`, `region`, `country`, `housedetails`, `created_at`, `updated_at`, `develop_finish_at`, `develop_begin_at`, `stage`) VALUES
(73, NULL, 52.721127, 41.433796, NULL, '54feeee0a125a6.83910404', 'Ð—Ð²ÐµÐ·Ð´Ð° 2', '84752513137', 'ÐšÐ¾Ð¼Ð¼ÑƒÐ½Ð°Ð»ÑŒÐ½Ð°Ñ ÑƒÐ»., 72Ð°', 'Ð¢Ð°Ð¼Ð±Ð¾Ð²', 'Ð¢Ð°Ð¼Ð±Ð¾Ð²ÑÐºÐ°Ñ Ð¾Ð±Ð»Ð°ÑÑ‚ÑŒ', 'Ð Ð¾ÑÑÐ¸Ñ', 'Ð¼Ð½Ð¾Ð³Ð¾ÑÑ‚Ð°Ð¶Ð½Ð¹ Ð´Ð¾Ð¼', '2015-04-15 07:36:14', '2015-04-15 08:54:17', '2016-04-15', '2014-04-15', 'build'),
(74, NULL, 52.721916, 41.441074, NULL, '54feeee0a125a6.83910404', 'ООО &#34;Рони&#34;', '8 4752 714709', 'Коммунальная ул., 42', 'Тамбов', 'Тамбовская область', 'Россия', '', '2015-04-15 08:56:33', '2015-04-15 09:21:12', '2016-04-15', '2015-04-15', 'build');

-- --------------------------------------------------------

--
-- Структура таблицы `users`
--

DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
  `uid` int(11) NOT NULL AUTO_INCREMENT,
  `unique_id` varchar(23) NOT NULL,
  `name` varchar(50)  NOT NULL,
  `email` varchar(100) NOT NULL,
  `encrypted_password` varchar(80)  NOT NULL,
  `salt` varchar(10) NOT NULL,
  `moderator` tinyint(1) NOT NULL DEFAULT '0',
  `created_at` datetime DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`uid`),
  UNIQUE KEY `unique_id` (`unique_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 AUTO_INCREMENT=4 ;

--
-- Дамп данных таблицы `users`
--

INSERT INTO `users` (`uid`, `unique_id`, `name`, `email`, `encrypted_password`, `salt`, `moderator`, `created_at`, `updated_at`) VALUES
(1, '54feeee0a125a6.83910404', 'Admin', 'anton.zheltov@gmail.com', 'miHjcE4BrzaAPnYYML9EDeXctzgzYzY0ODgxZWYw', '3c64881ef0', 1, '2015-03-10 09:17:20', NULL),
(2, '551e4940d37fc8.42433111', 'Ð¢ÐµÑÑ‚Ð¾Ð² Ð¢ÐµÑÑ‚', 'zheltov.aa@mrsk-1.ru', 'FEgXWuwn7Nxc0lmwn7YPmLZF4LUwYWJmNDk0NGM1', '0abf4944c5', 1, '2015-04-03 04:03:12', NULL);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
