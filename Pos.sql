CREATE DATABASE  IF NOT EXISTS `pos` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `pos`;
-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: pos
-- ------------------------------------------------------
-- Server version	8.0.39

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cajero`
--

DROP TABLE IF EXISTS cajero;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE cajero (
  id varchar(20) NOT NULL,
  nombre varchar(45) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cajero`
--

LOCK TABLES cajero WRITE;
/*!40000 ALTER TABLE cajero DISABLE KEYS */;
/*!40000 ALTER TABLE cajero ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categoria`
--

DROP TABLE IF EXISTS categoria;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE categoria (
  id varchar(10) NOT NULL,
  nombre varchar(30) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categoria`
--

LOCK TABLES categoria WRITE;
/*!40000 ALTER TABLE categoria DISABLE KEYS */;
INSERT INTO categoria VALUES ('001','Aguas'),('002','Dulces'),('003','Aceites'),('004','Vinos');
/*!40000 ALTER TABLE categoria ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cliente`
--

DROP TABLE IF EXISTS cliente;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE cliente (
  id varchar(100) NOT NULL,
  nombre varchar(45) DEFAULT NULL,
  telefono varchar(45) DEFAULT NULL,
  email varchar(45) DEFAULT NULL,
  descuento float unsigned zerofill DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cliente`
--

LOCK TABLES cliente WRITE;
/*!40000 ALTER TABLE cliente DISABLE KEYS */;
/*!40000 ALTER TABLE cliente ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `factura`
--

DROP TABLE IF EXISTS factura;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE factura (
  numero int NOT NULL AUTO_INCREMENT,
  cliente varchar(50) NOT NULL,
  cajero varchar(50) NOT NULL,
  fecha date NOT NULL,
  PRIMARY KEY (numero),
  KEY cliente (cliente),
  KEY cajero (cajero),
  CONSTRAINT factura_ibfk_1 FOREIGN KEY (cliente) REFERENCES cliente (id),
  CONSTRAINT factura_ibfk_2 FOREIGN KEY (cajero) REFERENCES cajero (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `factura`
--

LOCK TABLES factura WRITE;
/*!40000 ALTER TABLE factura DISABLE KEYS */;
/*!40000 ALTER TABLE factura ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `linea`
--

DROP TABLE IF EXISTS linea;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE linea (
  numero int NOT NULL AUTO_INCREMENT,
  factura int NOT NULL,
  producto varchar(50) NOT NULL,
  cantidad int NOT NULL,
  descuento float unsigned zerofill DEFAULT '000000000000',
  PRIMARY KEY (numero),
  KEY linea_ibfk_1 (factura),
  KEY linea_ibfk_2 (producto),
  CONSTRAINT linea_ibfk_1 FOREIGN KEY (factura) REFERENCES factura (numero),
  CONSTRAINT linea_ibfk_2 FOREIGN KEY (producto) REFERENCES producto (codigo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `linea`
--

LOCK TABLES linea WRITE;
/*!40000 ALTER TABLE linea DISABLE KEYS */;
/*!40000 ALTER TABLE linea ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS producto;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE producto (
  codigo varchar(10) NOT NULL,
  descripcion varchar(30) NOT NULL,
  unidadMedida varchar(20) DEFAULT NULL,
  precioUnitario float DEFAULT NULL,
  categoria varchar(10) NOT NULL,
  PRIMARY KEY (codigo),
  KEY producto_ibfk_2 (categoria),
  CONSTRAINT producto_ibfk_1 FOREIGN KEY (categoria) REFERENCES categoria (id),
  CONSTRAINT producto_ibfk_2 FOREIGN KEY (categoria) REFERENCES categoria (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto`
--

LOCK TABLES producto WRITE;
/*!40000 ALTER TABLE producto DISABLE KEYS */;
/*!40000 ALTER TABLE producto ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-10-10 19:43:20
