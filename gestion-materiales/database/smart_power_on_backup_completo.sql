CREATE DATABASE  IF NOT EXISTS `smart_power_on_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `smart_power_on_db`;
-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: smart_power_on_db
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `despacho`
--

DROP TABLE IF EXISTS `despacho`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `despacho` (
  `id_despacho` bigint NOT NULL AUTO_INCREMENT,
  `id_solicitud` bigint NOT NULL,
  `id_usuario_almacenero` bigint NOT NULL,
  `fecha_despacho` datetime DEFAULT CURRENT_TIMESTAMP,
  `tipo` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'PENDIENTE_CONFIRMACION',
  PRIMARY KEY (`id_despacho`),
  KEY `id_solicitud` (`id_solicitud`),
  KEY `id_usuario_almacenero` (`id_usuario_almacenero`),
  CONSTRAINT `despacho_ibfk_1` FOREIGN KEY (`id_solicitud`) REFERENCES `solicitud` (`id_solicitud`),
  CONSTRAINT `despacho_ibfk_2` FOREIGN KEY (`id_usuario_almacenero`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `despacho_chk_1` CHECK ((`tipo` in (_utf8mb4'COMPLETO',_utf8mb4'PARCIAL'))),
  CONSTRAINT `despacho_chk_2` CHECK ((`estado` in (_utf8mb4'PENDIENTE_CONFIRMACION',_utf8mb4'CONFIRMADO')))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `despacho`
--

LOCK TABLES `despacho` WRITE;
/*!40000 ALTER TABLE `despacho` DISABLE KEYS */;
INSERT INTO `despacho` VALUES (1,1,3,'2026-11-20 04:00:51','COMPLETO','CONFIRMADO'),(2,2,3,'2026-11-20 04:01:21','COMPLETO','CONFIRMADO');
/*!40000 ALTER TABLE `despacho` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_despacho`
--

DROP TABLE IF EXISTS `detalle_despacho`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_despacho` (
  `id_detalle_despacho` bigint NOT NULL AUTO_INCREMENT,
  `id_despacho` bigint NOT NULL,
  `id_detalle_solicitud` bigint NOT NULL,
  `id_material` bigint NOT NULL,
  `codigo_digitado` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cantidad_entregada` int NOT NULL,
  PRIMARY KEY (`id_detalle_despacho`),
  KEY `id_despacho` (`id_despacho`),
  KEY `id_detalle_solicitud` (`id_detalle_solicitud`),
  KEY `id_material` (`id_material`),
  CONSTRAINT `detalle_despacho_ibfk_1` FOREIGN KEY (`id_despacho`) REFERENCES `despacho` (`id_despacho`) ON DELETE CASCADE,
  CONSTRAINT `detalle_despacho_ibfk_2` FOREIGN KEY (`id_detalle_solicitud`) REFERENCES `detalle_solicitud` (`id_detalle_solicitud`),
  CONSTRAINT `detalle_despacho_ibfk_3` FOREIGN KEY (`id_material`) REFERENCES `material` (`id_material`),
  CONSTRAINT `detalle_despacho_chk_1` CHECK ((`cantidad_entregada` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_despacho`
--

LOCK TABLES `detalle_despacho` WRITE;
/*!40000 ALTER TABLE `detalle_despacho` DISABLE KEYS */;
INSERT INTO `detalle_despacho` VALUES (1,1,1,57,'SIVACON-8MF1-GEN',3),(2,2,2,203,'3004032',20),(3,2,3,202,'3004362',50);
/*!40000 ALTER TABLE `detalle_despacho` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_solicitud`
--

DROP TABLE IF EXISTS `detalle_solicitud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_solicitud` (
  `id_detalle_solicitud` bigint NOT NULL AUTO_INCREMENT,
  `id_solicitud` bigint NOT NULL,
  `id_material` bigint NOT NULL,
  `cantidad_solicitada` int NOT NULL,
  `cantidad_despachada` int DEFAULT '0',
  PRIMARY KEY (`id_detalle_solicitud`),
  KEY `id_solicitud` (`id_solicitud`),
  KEY `id_material` (`id_material`),
  CONSTRAINT `detalle_solicitud_ibfk_1` FOREIGN KEY (`id_solicitud`) REFERENCES `solicitud` (`id_solicitud`) ON DELETE CASCADE,
  CONSTRAINT `detalle_solicitud_ibfk_2` FOREIGN KEY (`id_material`) REFERENCES `material` (`id_material`),
  CONSTRAINT `detalle_solicitud_chk_1` CHECK ((`cantidad_solicitada` > 0)),
  CONSTRAINT `detalle_solicitud_chk_2` CHECK ((`cantidad_despachada` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_solicitud`
--

LOCK TABLES `detalle_solicitud` WRITE;
/*!40000 ALTER TABLE `detalle_solicitud` DISABLE KEYS */;
INSERT INTO `detalle_solicitud` VALUES (1,1,57,3,3),(2,2,203,20,20),(3,2,202,50,50);
/*!40000 ALTER TABLE `detalle_solicitud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entrega`
--

DROP TABLE IF EXISTS `entrega`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `entrega` (
  `id_entrega` bigint NOT NULL AUTO_INCREMENT,
  `id_despacho` bigint NOT NULL,
  `id_usuario_operario` bigint NOT NULL,
  `fecha_confirmacion` datetime DEFAULT CURRENT_TIMESTAMP,
  `confirmado` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id_entrega`),
  UNIQUE KEY `id_despacho` (`id_despacho`),
  KEY `id_usuario_operario` (`id_usuario_operario`),
  CONSTRAINT `entrega_ibfk_1` FOREIGN KEY (`id_despacho`) REFERENCES `despacho` (`id_despacho`),
  CONSTRAINT `entrega_ibfk_2` FOREIGN KEY (`id_usuario_operario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entrega`
--

LOCK TABLES `entrega` WRITE;
/*!40000 ALTER TABLE `entrega` DISABLE KEYS */;
INSERT INTO `entrega` VALUES (1,1,4,'2026-11-20 04:02:34',1),(2,2,4,'2026-11-20 04:02:39',1);
/*!40000 ALTER TABLE `entrega` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `material`
--

DROP TABLE IF EXISTS `material`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `material` (
  `id_material` bigint NOT NULL AUTO_INCREMENT,
  `codigo` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `especificacion_tecnica` text COLLATE utf8mb4_unicode_ci,
  `categoria` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `proyecto_asignado` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ubicacion` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_ingreso` datetime DEFAULT NULL,
  `precio_unitario` decimal(12,2) NOT NULL,
  `stock_actual` int DEFAULT '0',
  `stock_minimo` int DEFAULT '0',
  PRIMARY KEY (`id_material`),
  UNIQUE KEY `uk_material_codigo_proyecto` (`codigo`,`proyecto_asignado`),
  CONSTRAINT `material_chk_1` CHECK ((`stock_actual` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=205 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `material`
--

LOCK TABLES `material` WRITE;
/*!40000 ALTER TABLE `material` DISABLE KEYS */;
INSERT INTO `material` VALUES (11,'SIVACON-8MF1','Celda 8MF1 Control y Protección IP43','Siemens SIVACON 8MF1 800x800x2200mm con abatible tipo rack y puerta con vidrio','Tableros','PE-SAYA-S01-K2124','Zona Patio Techado - Bahía A','2026-02-12 09:30:00',4800.00,6,1),(12,'SIVACON-S8','Celda Tipo S8 Servicios Auxiliares VAC IP43','Siemens SIVACON S8 800x800x2200mm tablero de distribución SS.AA.','Tableros','PE-SAYA-S01-K2124','Zona Patio Techado - Bahía A','2026-02-12 09:30:00',4500.00,1,1),(13,'SPO-MK2C','Tablero MKs Dos Cuerpos Autosoportado','SPO 2000x1600x800mm tablero digital autosoportado','Tableros','PE-SAYA-S01-K2124','Zona Patio Techado - Bahía A','2026-02-12 09:30:00',3200.00,1,1),(14,'P1G902786','IO Box Siemens 6MD84 SIPROTEC 5','1/3x19\", 19 BI, 11 BO, IO207, PS201, 2x FO Ethernet 100Mbit/s IEC 61850','Siprotec 5','PE-SAYA-S01-K2124','Pasillo 1 - Anaquel 1 - Piso 2 (Climatizado)','2026-02-12 09:30:00',3900.00,2,1),(15,'P1C972307','Relé Protección Diferencial Línea 7SL87','Siemens SIPROTEC 5 1/2x19\", 11 BI, 9 BO, 4 CT, 4 VT, USART-AG-1LDFO 60km','Siprotec 5','PE-SAYA-S01-K2124','Pasillo 1 - Anaquel 1 - Piso 2 (Climatizado)','2026-02-12 09:30:00',8500.00,1,1),(16,'P1C972314','Relé Protección Diferencial Línea 7SL87 (Large)','Siemens SIPROTEC 5 1/2x19\" display grande, 11 BI, 9 BO, 4 CT, 4 VT, 60km FO','Siprotec 5','PE-SAYA-S01-K2124','Pasillo 1 - Anaquel 1 - Piso 2 (Climatizado)','2026-02-12 09:30:00',8900.00,1,1),(17,'P1G691383','Controlador de Bahía Siemens 6MD85','Siemens SIPROTEC 5 1/3x19\", 11 BI, 9 BO, 4 CT medición, 4 VT, ETH-BD-2FO','Siprotec 5','PE-SAYA-S01-K2124','Pasillo 1 - Anaquel 1 - Piso 2 (Climatizado)','2026-02-12 09:30:00',6200.00,1,1),(18,'P1N278850','Relé Registrador de Fallas Siemens 7KE85','Siemens SIPROTEC 5 1/3x19\", 11 BI, 9 BO, 4 CT, 4 VT, display grande','Siprotec 5','PE-SAYA-S01-K2124','Pasillo 1 - Anaquel 1 - Piso 2 (Climatizado)','2026-02-12 09:30:00',6800.00,1,1),(19,'P1M211536','Merging Unit Siemens 6MU85 (Base +175)','Siemens SIPROTEC 5 5/6x19\", 79 BI, 28 BO, 8 CT, 8 VT, IEC 61850-9-2','Siprotec 5','PE-SAYA-S01-K2124','Pasillo 1 - Anaquel 1 - Piso 2 (Climatizado)','2026-02-12 09:30:00',7400.00,1,1),(20,'P1M211543','Merging Unit Siemens 6MU85 (Base)','Siemens SIPROTEC 5 5/6x19\", 79 BI, 28 BO, 8 CT, 8 VT, IO202/207/208/230','Siprotec 5','PE-SAYA-S01-K2124','Pasillo 1 - Anaquel 1 - Piso 2 (Climatizado)','2026-02-12 09:30:00',7100.00,1,1),(21,'P1J2138916','Controlador SSAA Caseta Siemens 7SJ85','Siemens SIPROTEC 5 5/6x19\", 83 BI, 23 BO, 8 CT, 8 VT, Relé de Transferencia','Siprotec 5','PE-SAYA-S01-K2124','Pasillo 1 - Anaquel 1 - Piso 2 (Climatizado)','2026-02-12 09:30:00',6900.00,1,1),(22,'7VR5416-1TB20','Power Link IP Siemens 100W','Onda portadora PowerLink IP 110/230VAC, 110/220VDC, 2 puertos SFP multimodo','Onda portadora','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 1 - Piso 2','2026-02-12 09:30:00',9200.00,1,1),(23,'7VR9065','HF-Board 2-Coax 75 Ohm Siemens','Placa de radiofrecuencia para 2 coaxiales 75 Ohm accesorio PLC','Telecomunicaciones','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 1 - Piso 2','2026-02-12 09:30:00',850.00,1,1),(24,'6AV7863-6MA00','Monitor Industrial SIMATIC IFP1900 V2','Pantalla industrial Siemens 19\" multi-touch para sala de control','IHM','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 1 - Piso 2','2026-02-12 09:30:00',4100.00,2,1),(25,'METSEION92040','Medidor Multifunción Schneider ION9000','Medidor calidad de energía clase 0.1S con protocolos IEC 61850 / Modbus','Medidor','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 1 - Piso 2','2026-02-12 09:30:00',5600.00,1,1),(26,'5SY4110-7','Interruptor Termomagnético 1P 10A Curva C','Siemens SENTRON 230/400V 15kA, 1 polo, 10A','MCB','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 3 - Piso 1','2026-02-12 09:30:00',38.00,9,2),(27,'5SY4302-5','Interruptor Termomagnético 3P 2A Curva A','Siemens SENTRON 400V 10kA, 3 polos, 2A','MCB','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 3 - Piso 1','2026-02-12 09:30:00',95.00,2,1),(28,'5SY4304-5','Interruptor Termomagnético 3P 4A Curva C','Siemens SENTRON 400V 10kA, 3 polos, 4A','MCB','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 3 - Piso 1','2026-02-12 09:30:00',95.00,5,2),(29,'5SY4316-7','Interruptor Termomagnético 3P 16A Curva C','Siemens SENTRON 400V 10kA, 3 polos, 16A','MCB','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 3 - Piso 1','2026-02-12 09:30:00',92.00,2,1),(30,'5SY5202-7','Interruptor Automático Bipolar 2A DC/AC','Siemens SENTRON corriente universal 440VDC / 400VAC 10kA, 2 polos, 2A','MCB','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 3 - Piso 1','2026-02-12 09:30:00',110.00,2,1),(31,'5SY5204-7','Interruptor Automático Bipolar 4A DC/AC','Siemens SENTRON corriente universal 440VDC / 400VAC 10kA, 2 polos, 4A','MCB','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 3 - Piso 1','2026-02-12 09:30:00',110.00,33,5),(32,'5SY5206-7','Interruptor Automático Bipolar 6A DC/AC','Siemens SENTRON corriente universal 440VDC / 400VAC 10kA, 2 polos, 6A','MCB','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 3 - Piso 1','2026-02-12 09:30:00',110.00,20,4),(33,'5SY5210-7','Interruptor Automático Bipolar 10A DC/AC','Siemens SENTRON corriente universal 440VDC / 400VAC 10kA, 2 polos, 10A','MCB','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 3 - Piso 1','2026-02-12 09:30:00',110.00,4,2),(34,'5SY5216-7','Interruptor Automático Bipolar 16A DC/AC','Siemens SENTRON corriente universal 440VDC / 400VAC 10kA, 2 polos, 16A','MCB','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 3 - Piso 1','2026-02-12 09:30:00',115.00,2,1),(35,'5SY5232-7','Interruptor Automático Bipolar 32A DC/AC','Siemens SENTRON corriente universal 440VDC / 400VAC 10kA, 2 polos, 32A','MCB','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 3 - Piso 1','2026-02-12 09:30:00',125.00,3,1),(36,'5ST3012','Contacto Auxiliar Mini-Interruptor 2NC','Siemens SENTRON bloque auxiliar 2NC para series 5SL, 5SY, 5SP','MCB','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 3 - Piso 1','2026-02-12 09:30:00',32.00,102,15),(37,'3VA1180-3EF32','Interruptor Caja Moldeada 3VA1 80A 3P','Siemens SENTRON 3VA1 Frame 160, 25kA @ 415V, 3 polos, TM240','MCCB','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 3 - Piso 1','2026-02-12 09:30:00',680.00,2,1),(38,'7PA2251-0','Relé Biestable Siemens 125VCC 8CO','Relé rápido de disparo y bloqueo 8 contactos conmutables <10ms','Relé biestable','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 1 - Piso 2','2026-02-12 09:30:00',420.00,2,1),(39,'7PA2732-0AA00','Relé Monoestable Siemens 110/125VCC 4CO','Relé rápido de disparo <8ms con base montaje superficie 7XP9013-0','Relé monoestable','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 1 - Piso 2','2026-02-12 09:30:00',340.00,14,3),(40,'LP1D40008GD','Contactor Schneider TeSys Deca 60A 125VDC','Contactor tetrapolar 2NO + 2NC bobina 125VDC','Contactor','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 1 - Piso 2','2026-02-12 09:30:00',390.00,1,1),(41,'8MR2170-1A','Termostato Mecánico SIVACON 10-60°C','Siemens SIVACON contacto conmutado para control de calefacción','Termostato','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 1 - Piso 2','2026-02-12 09:30:00',85.00,9,2),(42,'8MR2170-1C','Higrostato Mecánico SIVACON 35-95% HR','Siemens SIVACON 5A 250VAC control de humedad relativa','Higrostato','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 1 - Piso 2','2026-02-12 09:30:00',145.00,9,2),(43,'8MR2130-6A','Calefactor SIVACON 60W 120-240V','Resistencia calefactora semiconductora HG140 para tablero','Resistencia','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 1 - Piso 2','2026-02-12 09:30:00',120.00,7,2),(44,'8MR6423-5VL64','Ventilador de Techo SIVACON 230V IP54','Flujo 232 m3/h, recorte 292x292mm RAL 7035','Extractor','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 1 - Piso 2','2026-02-12 09:30:00',460.00,4,1),(45,'3SU1052-2BF10','Selector Iluminable SIRIUS ACT 22mm','Siemens 2 posiciones O-I sostenido metal brillante negro','Selector','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 1 - Piso 2','2026-02-12 09:30:00',68.00,9,2),(46,'2904600','Fuente Alimentación Phoenix QUINT4 24VDC/5A','Fuente primaria 1AC/24VDC 5A con tecnología SFB e interfaz NFC','Fuente','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 1 - Piso 2','2026-02-12 09:30:00',540.00,2,1),(47,'1466214','Descargador Sobretensiones Phoenix Tipo 2','VAL-SPP-T2-275-3+1-UT-R sistema 5 conductores con contacto remoto','Descargador','PE-SAYA-S01-K2124','Pasillo 2 - Anaquel 1 - Piso 2','2026-02-12 09:30:00',490.00,3,1),(48,'3004362','Borne Sencillo Phoenix UK 5 N (57A)','Borne de paso gris 800V conexión por tornillo riel NS 35','Bornera','PE-SAYA-S01-K2124','Pasillo 3 - Gaveta 04 - Nivel 1','2026-02-12 09:30:00',4.20,825,100),(49,'3004032','Borne Seccionable con Cuchilla UK5-MTK P/P','Borne de prueba para circuitos de control 16A 500V con tomas','Bornera','PE-SAYA-S01-K2124','Pasillo 3 - Gaveta 04 - Nivel 1','2026-02-12 09:30:00',11.50,186,30),(50,'2775184','Borne de Tierra Phoenix UDK 4-PE','Borne conductor protección verde-amarillo doble conexión riel DIN','Bornera','PE-SAYA-S01-K2124','Pasillo 3 - Gaveta 04 - Nivel 1','2026-02-12 09:30:00',9.80,90,20),(51,'SIVACON-8MF1','Celda 8MF1 Telecomunicaciones IP43','Siemens SIVACON 8MF1 800x800x2200mm para sala de telecomunicaciones Yarabamba','Tableros','PE-SAYA-S02-K7730','Zona Patio Techado - Bahía B','2026-02-26 10:15:00',4800.00,1,1),(52,'7VR5416-1TB20','Power Link IP Siemens 100W','Onda portadora PowerLink IP 110/230VAC, 110/220VDC, 2 puertos SFP multimodo','Onda portadora','PE-SAYA-S02-K7730','Pasillo 1 - Anaquel 2 - Piso 1','2026-02-26 10:20:00',9200.00,1,1),(53,'RST2228','Switch Industrial RUGGEDCOM RST2228','Switch Capa 2 montaje rack 19\", 24x 10/100/1000TX + 4x 10G SFP+, fuente redundante','Telecomunicaciones','PE-SAYA-S02-K7730','Pasillo 1 - Anaquel 2 - Piso 2','2026-02-26 10:25:00',6400.00,1,1),(54,'RX1536','Router Modular RUGGEDCOM RX1536','Router Capa 3 modular montaje rack 19\", módulos ópticos WAN/LAN','Telecomunicaciones','PE-SAYA-S02-K7730','Pasillo 1 - Anaquel 2 - Piso 2','2026-02-26 10:30:00',7800.00,1,1),(55,'5SY5204-7','Interruptor Automático Bipolar 4A DC/AC','Siemens SENTRON corriente universal 440VDC / 400VAC 10kA, 2 polos, 4A','MCB','PE-SAYA-S02-K7730','Pasillo 2 - Anaquel 4 - Piso 1','2026-02-26 11:00:00',110.00,18,4),(56,'3004362','Borne Sencillo Phoenix UK 5 N (57A)','Borne de paso gris 800V conexión por tornillo riel NS 35','Bornera','PE-SAYA-S02-K7730','Pasillo 3 - Gaveta 08 - Nivel 1','2026-02-26 11:15:00',4.20,410,80),(57,'SIVACON-8MF1-GEN','Celda 8MF1 Control y Protección IP55','Siemens SIVACON 8MF1 800x800x2200mm con abatible tipo rack y puerta con vidrio','Tableros','PE-BETA-S01-K2124','Zona Patio Techado - Bahía A1','2026-01-15 08:30:00',0.00,32,4),(58,'SIVACON-S8-GEN','Celda Tipo S8 Servicios Auxiliares VAC IP55','Siemens SIVACON S8 800x800x2200mm tablero de distribución SS.AA.','Tableros','PE-BETA-S01-K2124','Zona Patio Techado - Bahía A2','2026-01-15 08:40:00',0.00,11,2),(59,'SPO-MK2C-GEN','MKs Dos Cuerpos Autosoportado','SPO 1900x1600x800mm tablero digital patio','Tableros','PE-BETA-S01-K2124','Zona Patio Techado - Bahía A3','2026-01-15 09:00:00',0.00,24,3),(60,'SPO-CAJA-CTVT','Caja de Agrupamiento Corrientes y Tensiones','SPO 750x500x250mm acero galvanizado IP55','Tableros','PE-PICO-S04-K2124','Zona Patio Techado - Bahía E2','2026-02-12 08:45:00',0.00,4,1),(61,'SPO-MK-CR','MK Cambio Rápido','SPO 1900x1600x1000mm tablero patio','Tableros','PE-BETA-S01-K2124','Zona Patio Techado - Bahía A4','2026-01-15 09:10:00',0.00,2,1),(62,'8MR2130-6A','Calefactor SIVACON 120-240V 60W','Resistencia calefactora Siemens para tablero','Accesorios','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 4 - Piso 1','2026-01-16 10:00:00',0.00,45,5),(63,'8MR2130-0A','Calefactor SIVACON 120-240V 100W','Resistencia calefactora Siemens 100W','Accesorios','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 4 - Piso 1','2026-01-16 10:05:00',0.00,57,6),(64,'8MR2170-1A','Termostato Mecánico 10 a 60 °C','Siemens contacto conmutado para calefacción','Accesorios','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 4 - Piso 2','2026-01-16 10:10:00',0.00,98,10),(65,'8MR2170-1C','Higrostato Mecánico 35 a 95% HR','Siemens 5A 250VAC control humedad relativa','Accesorios','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 4 - Piso 2','2026-01-16 10:15:00',0.00,102,10),(66,'8MR6423-5VL64','Ventilador de Techo IP54 232 m3/h','Siemens RAL 7035 recorte 292x292mm 230VAC','Accesorios','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 5 - Piso 1','2026-01-16 10:20:00',0.00,15,2),(67,'8MR6523-5LV30','Ventilador con Filtro IP55 105 m3/h','Siemens RAL 7035 recorte 177x177mm 230VAC','Accesorios','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 5 - Piso 1','2026-01-16 10:25:00',0.00,52,5),(68,'8MR6500-5GV30','Filtro de Salida IP55','Siemens RAL 7035 recorte 177x177mm','Accesorios','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 5 - Piso 2','2026-01-16 10:30:00',0.00,52,5),(69,'LED-E27-220V','Lámpara LED Casquillo E27 4500K','Lámpara interior tablero fijación magnética 220VAC','Accesorios','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 5 - Piso 2','2026-01-16 10:35:00',0.00,102,10),(70,'3SU1052-2BF10-0AA0','Selector Iluminable SIRIUS ACT 22mm','Siemens 2 posiciones O-I sostenido negro','Accesorios','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 6 - Piso 1','2026-01-17 09:00:00',0.00,102,10),(71,'3SU1400-1AA10-1BA0','Módulo de Contactos 1NA SIRIUS ACT','Siemens bornes de tornillo fijación frontal','Accesorios','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 6 - Piso 1','2026-01-17 09:05:00',0.00,102,10),(72,'3SE5250-0CC05','Interruptor Fin de Carrera Puerta','Siemens 1NA + 1NC carrera normal','Accesorios','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 6 - Piso 2','2026-01-17 09:10:00',0.00,102,10),(73,'3SU1550-0AA10-0AA0','Soporte para Tres Módulos Metal','Siemens SIRIUS ACT soporte metálico','Accesorios','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 6 - Piso 2','2026-01-17 09:15:00',0.00,116,12),(74,'5SY4110-7','Interruptor Termomagnético 1P 10A Curva C','Siemens SENTRON 230/400V 15kA 1 polo','MCB','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 1 - Piso 1','2026-01-18 11:00:00',0.00,102,10),(75,'5SY5202-7','Interruptor Bipolar 2A DC/AC','Siemens SENTRON 440VDC/400VAC 10kA 2 polos','MCB','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 1 - Piso 1','2026-01-18 11:05:00',0.00,8,2),(76,'5SY5204-7','Interruptor Bipolar 4A DC/AC','Siemens SENTRON 440VDC/400VAC 10kA 2 polos','MCB','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 1 - Piso 2','2026-01-18 11:10:00',0.00,147,15),(77,'5SY5206-7','Interruptor Bipolar 6A DC/AC','Siemens SENTRON 440VDC/400VAC 10kA 2 polos','MCB','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 1 - Piso 2','2026-01-18 11:15:00',0.00,312,30),(78,'5SY5210-7','Interruptor Bipolar 10A DC/AC','Siemens SENTRON 440VDC/400VAC 10kA 2 polos','MCB','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 1 - Piso 3','2026-01-18 11:20:00',0.00,82,10),(79,'5SY5216-7','Interruptor Bipolar 16A DC/AC','Siemens SENTRON 440VDC/400VAC 10kA 2 polos','MCB','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 1 - Piso 3','2026-01-18 11:25:00',0.00,98,10),(80,'5SY5220-7','Interruptor Bipolar 20A DC/AC','Siemens SENTRON 440VDC/400VAC 10kA 2 polos','MCB','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 1 - Piso 3','2026-01-18 11:30:00',0.00,14,2),(81,'5ST3012','Contacto Auxiliar Mini-Interruptor 2NC','Siemens SENTRON bloque auxiliar para 5SL/5SY/5SP','MCB','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 2 - Piso 1','2026-01-18 11:35:00',0.00,1034,100),(82,'5ST3022','Contacto de Disparo Mini-Interruptor 2NC','Siemens SENTRON señalización disparo posición','MCB','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 2 - Piso 1','2026-01-18 11:40:00',0.00,104,10),(83,'5SY4316-7','Interruptor Termomagnético 3P 16A Curva C','Siemens SENTRON 400V 10kA 3 polos','MCB','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 2 - Piso 2','2026-01-18 11:45:00',0.00,50,5),(84,'5SY4302-5','Interruptor Termomagnético 3P 2A Curva A','Siemens SENTRON 400V 10kA 3 polos','MCB','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 2 - Piso 2','2026-01-18 11:50:00',0.00,88,10),(85,'5SY7316-7','Interruptor Termomagnético 3P 16A 15kA','Siemens SENTRON 400V 15kA Curva C 3 polos','MCB','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 2 - Piso 3','2026-01-18 11:55:00',0.00,37,4),(86,'5SY7116-7','Interruptor Termomagnético 1P 16A 15kA','Siemens SENTRON 230/400V 15kA Curva C 1 polo','MCB','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 2 - Piso 3','2026-01-18 12:00:00',0.00,21,3),(87,'5SY4304-5','Interruptor Termomagnético 3P 4A Curva C','Siemens SENTRON 400V 10kA 3 polos','MCB','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 2 - Piso 3','2026-01-18 12:05:00',0.00,22,3),(88,'3VA1163-3EF32-0AA0','Interruptor Caja Moldeada 3VA1 63A 3P','Siemens SENTRON Frame 160 25kA@415V TM240','MCCB','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 3 - Piso 1','2026-01-19 09:00:00',0.00,12,2),(89,'3VA9988-0AA11','Bloque Contactos Auxiliares 3VA1/2 HP','Siemens contacto conmutado 14mm para 3VA','MCCB','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 3 - Piso 1','2026-01-19 09:05:00',0.00,31,4),(90,'3VA9988-0AB12','Bloque Señalización Disparo 3VA1/2 HQ','Siemens contacto señalización 7mm para 3VA','MCCB','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 3 - Piso 1','2026-01-19 09:10:00',0.00,32,4),(91,'3VA2340-5HL32-0AA0','Interruptor Caja Moldeada 3VA2 400A 3P','Siemens SENTRON Frame 400 55kA@415V ETU320','MCCB','PE-BETA-S01-K2124','Pasillo 2 - Anaquel 3 - Piso 2','2026-01-19 09:15:00',0.00,10,2),(92,'7PA2732-0AA00-2','Relé Monoestable 110/125VCC 4CO','Siemens tiempo op <8ms con base 7XP9013-0','Accesorios','PE-BETA-S01-K2124','Pasillo 1 - Anaquel 5 - Piso 1','2026-01-20 09:00:00',0.00,84,8),(93,'7PA2632-0AA00-2','Relé Monoestable 110/125VCC 8CO','Siemens tiempo op 20ms con base 7XP9012-0','Accesorios','PE-BETA-S01-K2124','Pasillo 1 - Anaquel 5 - Piso 1','2026-01-20 09:05:00',0.00,8,2),(94,'7PA2251-0','Relé Biestable 125VCC 8CO','Siemens relé de disparo y bloqueo 8 contactos','Accesorios','PE-BETA-S01-K2124','Pasillo 1 - Anaquel 5 - Piso 2','2026-01-20 09:10:00',0.00,32,4),(95,'7XP9012-0','Base Montaje Bandeja Relé 7PA22/7PA26','Siemens zócalo montaje superficie','Accesorios','PE-BETA-S01-K2124','Pasillo 1 - Anaquel 5 - Piso 2','2026-01-20 09:15:00',0.00,32,4),(96,'LP1D40008GD','Contactor Schneider TeSys Deca 60A 125VDC','Contactor tetrapolar 2NO + 2NC bobina 125VDC','Accesorios','PE-BETA-S01-K2124','Pasillo 1 - Anaquel 5 - Piso 2','2026-01-20 09:20:00',0.00,4,1),(97,'P1G902786','IO Box Siemens 6MD84 SIPROTEC 5','1/3x19\" 19 BI, 11 BO, IO207, PS201, ETH-BD-2FO IEC 61850','Siprotec 5','PE-BETA-S01-K2124','Pasillo 1 - Anaquel 1 - Piso 1','2026-01-21 09:00:00',0.00,8,2),(98,'P1M134569','Merging Unit Siemens 6MU85 (5/6x19\")','SIPROTEC 5 83 BI, 41 BO, 4 CT, 4 VT, ETH-BB-2FO','Siprotec 5','PE-BETA-S01-K2124','Pasillo 1 - Anaquel 1 - Piso 1','2026-01-21 09:05:00',0.00,16,2),(99,'P1M211680','Merging Unit Siemens 6MU85 (2/3x19\" 4CT)','SIPROTEC 5 71 BI, 41 BO, 4 CT protección, ETH-BB-2FO','Siprotec 5','PE-BETA-S01-K2124','Pasillo 1 - Anaquel 1 - Piso 2','2026-01-21 09:10:00',0.00,8,2),(100,'P1M141420','Merging Unit Siemens 6MU85 (1/2x19\")','SIPROTEC 5 19 BI, 9 BO, 4 CT, 8 VT, ETH-BB-2FO','Siprotec 5','PE-BETA-S01-K2124','Pasillo 1 - Anaquel 1 - Piso 2','2026-01-21 09:15:00',0.00,8,2),(101,'P1C734479','Relé Diferencial Línea Siemens 7SL87 100km','SIPROTEC 5 1/2x19\" 11 BI, 9 BO, 4 CT, 4 VT, FO 1550nm','Siprotec 5','PE-BETA-S01-K2124','Pasillo 1 - Anaquel 2 - Piso 1','2026-01-21 09:20:00',0.00,6,1),(102,'P1C734486','Relé Diferencial Línea Siemens 7SL87 Large','SIPROTEC 5 1/2x19\" display grande, FO 100km 1550nm','Siprotec 5','PE-BETA-S01-K2124','Pasillo 1 - Anaquel 2 - Piso 1','2026-01-21 09:25:00',0.00,6,1),(103,'P1E918428','Relé Protección de Barras Siemens 7SS85','SIPROTEC 5 1/3x19\" 19 BI, 11 BO, Base + 150 puntos','Siprotec 5','PE-BETA-S01-K2124','Pasillo 1 - Anaquel 2 - Piso 2','2026-01-21 09:30:00',0.00,4,1),(104,'P1N278850','Relé Registrador de Fallas Siemens 7KE85','SIPROTEC 5 1/3x19\" 11 BI, 9 BO, 4 CT, 4 VT, display grande','Siprotec 5','PE-BETA-S01-K2124','Pasillo 1 - Anaquel 2 - Piso 2','2026-01-21 09:35:00',0.00,5,1),(105,'6AV7863-6MA00-2AA0','Monitor Industrial SIMATIC IFP1900 V2','Pantalla industrial Siemens 19\" multi-touch','Telecomunicaciones','PE-BETA-S01-K2124','Pasillo 1 - Anaquel 3 - Piso 1','2026-01-22 10:00:00',0.00,8,2),(106,'RX1536-GEN','Router Modular RUGGEDCOM RX1536','Siemens 6GK6015-0DM23-0DC0 Capa 3 rack 19\"','Telecomunicaciones','PE-BETA-S01-K2124','Pasillo 1 - Anaquel 3 - Piso 1','2026-01-22 10:10:00',0.00,8,2),(107,'RST2228-BUS','Switch Bus de Proceso RUGGEDCOM RST2228','Siemens 6GK6222-6AB00-5CN0 puertos FO multimodo','Telecomunicaciones','PE-BETA-S01-K2124','Pasillo 1 - Anaquel 3 - Piso 2','2026-01-22 10:15:00',0.00,20,3),(108,'RSG909R-GEN','RedBox RUGGEDCOM RSG909R HSR/PRP','Siemens 6GK6498-0RB00-1CN0 redundancia IEC 62439-3','Telecomunicaciones','PE-BETA-S01-K2124','Pasillo 1 - Anaquel 3 - Piso 2','2026-01-22 10:20:00',0.00,10,2),(109,'RMC40-GEN','Conversor de Medios RUGGEDCOM RMC40','Siemens 6GK6004-0AC03-0BA0 Ethernet a Fibra Óptica','Telecomunicaciones','PE-BETA-S01-K2124','Pasillo 1 - Anaquel 4 - Piso 1','2026-01-22 10:25:00',0.00,24,3),(110,'2904600','Fuente Alimentación Phoenix QUINT4 24VDC/5A','Fuente primaria 1AC/24VDC 5A tecnología SFB','Accesorios','PE-BETA-S01-K2124','Pasillo 1 - Anaquel 4 - Piso 1','2026-01-22 10:30:00',0.00,10,2),(111,'1466214','Descargador Sobretensiones Phoenix Tipo 2','VAL-SPP-T2-275-3+1-UT-R 5 conductores con contacto remoto','Accesorios','PE-BETA-S01-K2124','Pasillo 1 - Anaquel 4 - Piso 2','2026-01-22 10:35:00',0.00,20,3),(112,'TC-E-VA-2.2','Bloque de Prueba Tensión ESSAILEC','Bloque prueba señales de tensión en medidores','Accesorios','PE-BETA-S01-K2124','Pasillo 1 - Anaquel 4 - Piso 2','2026-01-22 10:40:00',0.00,22,3),(113,'CC-E-VA-6.6','Bloque de Prueba Corriente ESSAILEC','Bloque prueba señales de corriente en medidores','Accesorios','PE-BETA-S01-K2124','Pasillo 1 - Anaquel 4 - Piso 2','2026-01-22 10:45:00',0.00,22,3),(114,'5WSML-2C','Caja de Fibra Pared SDX Doble Puerta','Leviton montaje en pared sin seguro','Telecomunicaciones','PE-BETA-S01-K2124','Pasillo 4 - Anaquel 1 - Piso 1','2026-01-23 11:00:00',0.00,52,5),(115,'5F100-4QL','Placa Adaptadora Fibra SDX Quad LC 24F','Leviton OM3/OM4 Aqua manga cerámica circonia','Telecomunicaciones','PE-BETA-S01-K2124','Pasillo 4 - Anaquel 1 - Piso 1','2026-01-23 11:05:00',0.00,71,8),(116,'PC-FO-OM3-3M','Patch Cord F/O Multimodo OM3 LC-LC 3m','Microlink Duplex 3mm OM3 3 metros','Telecomunicaciones','PE-BETA-S01-K2124','Pasillo 4 - Anaquel 1 - Piso 2','2026-01-23 11:10:00',0.00,340,30),(117,'PC-FO-OM3-5M','Patch Cord F/O Multimodo OM3 LC-LC 5m','Microlink Duplex 3mm OM3 5 metros','Telecomunicaciones','PE-BETA-S01-K2124','Pasillo 4 - Anaquel 1 - Piso 2','2026-01-23 11:15:00',0.00,114,15),(118,'VT-220-110V','Transformador Puro Monofásico Medida 20VA','Entrada 0/220V Salida 0/110V 60Hz uso interior','Accesorios','PE-BETA-S01-K2124','Pasillo 4 - Anaquel 2 - Piso 1','2026-01-23 11:20:00',0.00,30,4),(119,'2277912','Transformador Corriente Barra Pasante 500/5A','Phoenix PACT MCR-V2-6015-85 Clase 1 5VA','Accesorios','PE-BETA-S01-K2124','Pasillo 4 - Anaquel 2 - Piso 1','2026-01-23 11:25:00',0.00,18,3),(120,'3004362-TOT','Borne Sencillo Phoenix UK 5 N (57A)','Borne de paso gris 800V 57A cable 4-32 AWG riel DIN','Bornera','PE-BETA-S01-K2124','Pasillo 3 - Gaveta 01 - Nivel 1','2026-01-24 14:00:00',0.00,9517,500),(121,'3004032-TOT','Borne Seccionable Cuchilla UK5-MTK-P/P','Borne prueba circuitos control 16A 500V Phoenix','Bornera','PE-BETA-S01-K2124','Pasillo 3 - Gaveta 02 - Nivel 1','2026-01-24 14:05:00',0.00,11372,500),(122,'3004524','Borne de Paso Phoenix UK 6 N (41A)','Borne gris 800V sección 6mm2 conexión por tornillo','Bornera','PE-BETA-S01-K2124','Pasillo 3 - Gaveta 03 - Nivel 1','2026-01-24 14:10:00',0.00,2663,200),(123,'3005073','Borne Sencillo Phoenix UK 10 N (57A)','Borne protección gris 800V ancho 10.2mm','Bornera','PE-BETA-S01-K2124','Pasillo 3 - Gaveta 03 - Nivel 2','2026-01-24 14:15:00',0.00,48,10),(124,'2775184-TOT','Borne de Tierra Phoenix UDK 4-PE','Borne verde-amarillo 24-10 AWG ancho 6.2mm','Bornera','PE-BETA-S01-K2124','Pasillo 3 - Gaveta 04 - Nivel 1','2026-01-24 14:20:00',0.00,267,30),(125,'0311126','Borne Seccionable y Prueba URTK/SP Gris','Phoenix 300VAC 45A 26-8 AWG circuitos CT/VT','Bornera','PE-BETA-S01-K2124','Pasillo 3 - Gaveta 04 - Nivel 2','2026-01-24 14:25:00',0.00,1630,150),(126,'3070516','Borne Seccionable y Prueba URTK/SP Azul','Phoenix 300VAC 45A 26-8 AWG color azul neutro','Bornera','PE-BETA-S01-K2124','Pasillo 3 - Gaveta 04 - Nivel 2','2026-01-24 14:30:00',0.00,1349,120),(127,'0706058','Borne Interrumpible URTK/SP RD Rojo','Phoenix eslabón deslizante 500V 41A color rojo','Bornera','PE-BETA-S01-K2124','Pasillo 3 - Gaveta 05 - Nivel 1','2026-01-24 14:35:00',0.00,294,30),(128,'3046375','Borne Puente Separación UT 2,5-MT-P/P','Phoenix terminal prueba 15A 600V 22-12 AWG','Bornera','PE-BETA-S01-K2124','Pasillo 3 - Gaveta 05 - Nivel 2','2026-01-24 14:40:00',0.00,1682,150),(129,'3003020','Tapa Final Borne D-UK 4/10 Gris','Phoenix longitud 42.5mm anchura 1.8mm color gris','Bornera','PE-BETA-S01-K2124','Pasillo 3 - Gaveta 06 - Nivel 1','2026-01-24 14:45:00',0.00,1293,100),(130,'1201442','Tope Final Universal Phoenix E/UK','Soporte final riel simétrico NS 32 / NS 35','Bornera','PE-BETA-S01-K2124','Pasillo 3 - Gaveta 06 - Nivel 1','2026-01-24 14:50:00',0.00,2116,200),(131,'1004306','Marcador de Bornera Phoenix KLM','Portamarcador grupo borneras Phoenix Contact','Bornera','PE-BETA-S01-K2124','Pasillo 3 - Gaveta 06 - Nivel 2','2026-01-24 14:55:00',0.00,2116,200),(132,'0203250','Puente Fijo 10 Polos FBI 10-6','Phoenix puente para bornes UK 5 N recortable','Bornera','PE-BETA-S01-K2124','Pasillo 3 - Gaveta 07 - Nivel 1','2026-01-24 15:00:00',0.00,619,60),(133,'0203263','Puente Fijo 10 Polos FBI 10-8','Phoenix paso 8mm 10 polos plateado','Bornera','PE-BETA-S01-K2124','Pasillo 3 - Gaveta 07 - Nivel 1','2026-01-24 15:05:00',0.00,345,35),(134,'0200020','Puente Fijo 2 Polos FBI 2-8','Phoenix paso 8mm 2 polos plateado','Bornera','PE-BETA-S01-K2124','Pasillo 3 - Gaveta 07 - Nivel 2','2026-01-24 15:10:00',0.00,612,60),(135,'1051003','Tiras Marcación Borneras ZB 6 Sin Imprimir','Phoenix ancho 6.2mm tarjetas marcación blanco','Bornera','PE-BETA-S01-K2124','Pasillo 3 - Gaveta 08 - Nivel 1','2026-01-24 15:15:00',0.00,991,100),(136,'1052002','Tiras Marcación Borneras ZB 8 Sin Imprimir','Phoenix ancho 8.2mm tarjetas marcación blanco','Bornera','PE-BETA-S01-K2124','Pasillo 3 - Gaveta 08 - Nivel 1','2026-01-24 15:20:00',0.00,286,30),(137,'SIVACON-8MF1','Celda 8MF1 Control y Protección IP55','Siemens SIVACON 8MF1 800x800x2200mm con abatible rack','Tableros','PE-BETA-S03-K2124','Zona Patio Techado - Bahía B1','2026-01-25 09:00:00',0.00,4,1),(138,'SIVACON-S8','Celda Tipo S8 Servicios Auxiliares VAC IP55','Siemens SIVACON S8 800x800x2200mm','Tableros','PE-BETA-S03-K2124','Zona Patio Techado - Bahía B2','2026-01-25 09:10:00',0.00,1,1),(139,'SPO-MK2C','MKs Dos Cuerpos Autosoportado','SPO 1900x1600x800mm tablero patio','Tableros','PE-BETA-S03-K2124','Zona Patio Techado - Bahía B3','2026-01-25 09:20:00',0.00,2,1),(140,'P1G902786','IO Box Siemens 6MD84 SIPROTEC 5','1/3x19\" 19 BI, 11 BO, IO207, PS201, ETH-BD-2FO','Siprotec 5','PE-BETA-S03-K2124','Pasillo 1 - Anaquel 3 - Piso 1','2026-01-26 10:00:00',0.00,2,1),(141,'P1M134569','Merging Unit Siemens 6MU85 (5/6x19\")','SIPROTEC 5 83 BI, 41 BO, 4 CT, 4 VT, ETH-BB-2FO','Siprotec 5','PE-BETA-S03-K2124','Pasillo 1 - Anaquel 3 - Piso 1','2026-01-26 10:10:00',0.00,4,1),(142,'P1J2138916','Controlador SSAA Caseta Siemens 6MD85','SIPROTEC 5 1x19\" 83 BI, 23 BO, 8 CT, 8 VT','Siprotec 5','PE-BETA-S03-K2124','Pasillo 1 - Anaquel 3 - Piso 2','2026-01-26 10:20:00',0.00,1,1),(143,'P1C734479','Relé Diferencial Línea Siemens 7SL87','SIPROTEC 5 1/2x19\" 11 BI, 9 BO, FO 100km 1550nm','Siprotec 5','PE-BETA-S03-K2124','Pasillo 1 - Anaquel 3 - Piso 2','2026-01-26 10:30:00',0.00,2,1),(144,'P1G691383','Controlador de Bahía Siemens 6MD85','SIPROTEC 5 1/3x19\" 11 BI, 9 BO, display grande','Siprotec 5','PE-BETA-S03-K2124','Pasillo 1 - Anaquel 3 - Piso 2','2026-01-26 10:40:00',0.00,2,1),(145,'P1N278850','Relé Registrador de Fallas Siemens 7KE85','SIPROTEC 5 1/3x19\" 11 BI, 9 BO, display grande','Siprotec 5','PE-BETA-S03-K2124','Pasillo 1 - Anaquel 3 - Piso 2','2026-01-26 10:50:00',0.00,1,1),(146,'RST2228-BUS','Switch Bus de Proceso RUGGEDCOM RST2228','Siemens 6GK6222-6AB00-5CN0 montaje rack 19\"','Telecomunicaciones','PE-BETA-S03-K2124','Pasillo 1 - Anaquel 4 - Piso 1','2026-01-27 09:00:00',0.00,2,1),(147,'METSEION92040','Medidor Multifunción Schneider ION 9000','Medidor calidad energía clase 0.1S IEC 61850','Medidor','PE-BETA-S03-K2124','Pasillo 1 - Anaquel 4 - Piso 2','2026-01-27 09:15:00',0.00,2,1),(148,'5SY5206-7','Interruptor Bipolar 6A DC/AC','Siemens SENTRON 440VDC / 400VAC 10kA 2 polos','MCB','PE-BETA-S03-K2124','Pasillo 2 - Anaquel 1 - Piso 1','2026-01-28 15:00:00',0.00,29,5),(149,'5ST3012','Contacto Auxiliar Mini-Interruptor 2NC','Siemens SENTRON bloque auxiliar 2NC','MCB','PE-BETA-S03-K2124','Pasillo 2 - Anaquel 1 - Piso 2','2026-01-28 15:10:00',0.00,109,15),(150,'3004362','Borne Sencillo Phoenix UK 5 N (57A)','Borne de paso gris 800V conexión por tornillo','Bornera','PE-BETA-S03-K2124','Pasillo 3 - Gaveta 03 - Nivel 1','2026-01-28 15:20:00',0.00,1028,100),(151,'3004032','Borne Seccionable Cuchilla UK5-MTK P/P','Borne de prueba 16A 500V circuitos de control','Bornera','PE-BETA-S03-K2124','Pasillo 3 - Gaveta 03 - Nivel 2','2026-01-28 15:30:00',0.00,1012,100),(152,'SIVACON-8MF1','Celda 8MF1 Control y Protección IP55','Siemens SIVACON 8MF1 800x800x2200mm con abatible rack','Tableros','PE-PICO-S01-K2124','Zona Patio Techado - Bahía C1','2026-02-02 08:45:00',0.00,12,2),(153,'SIVACON-S8','Celda Tipo S8 Servicios Auxiliares VAC IP55','Siemens SIVACON S8 800x800x2200mm','Tableros','PE-PICO-S01-K2124','Zona Patio Techado - Bahía C2','2026-02-02 09:00:00',0.00,3,1),(154,'SPO-MK2C','MKs Dos Cuerpos Autosoportado','SPO 1900x1600x800mm tablero patio','Tableros','PE-PICO-S01-K2124','Zona Patio Techado - Bahía C3','2026-02-02 09:15:00',0.00,10,2),(155,'P1G902786','IO Box Siemens 6MD84 SIPROTEC 5','1/3x19\" 19 BI, 11 BO, IO207, PS201, ETH-BD-2FO','Siprotec 5','PE-PICO-S01-K2124','Pasillo 1 - Anaquel 4 - Piso 1','2026-02-03 09:00:00',0.00,2,1),(156,'P1M134569','Merging Unit Siemens 6MU85 (5/6x19\")','SIPROTEC 5 83 BI, 41 BO, 4 CT, 4 VT, ETH-BB-2FO','Siprotec 5','PE-PICO-S01-K2124','Pasillo 1 - Anaquel 4 - Piso 1','2026-02-03 09:15:00',0.00,6,1),(157,'P1M211697','Merging Unit Siemens 6MU85 (2/3x19\")','SIPROTEC 5 71 BI, 25 BO, 4 CT, 4 VT, ETH-BB-2FO','Siprotec 5','PE-PICO-S01-K2124','Pasillo 1 - Anaquel 4 - Piso 1','2026-02-03 09:30:00',0.00,10,2),(158,'P1C973120','Relé Diferencial Línea Siemens 7SL86','SIPROTEC 5 1/2x19\" 11 BI, 9 BO, 4 CT, 4 VT, FO 24km','Siprotec 5','PE-PICO-S01-K2124','Pasillo 1 - Anaquel 4 - Piso 2','2026-02-03 09:45:00',0.00,4,1),(159,'P1C972857','Relé Diferencial Línea Siemens 7SL87','SIPROTEC 5 1/2x19\" 11 BI, 9 BO, FO 100km 1550nm','Siprotec 5','PE-PICO-S01-K2124','Pasillo 1 - Anaquel 4 - Piso 2','2026-02-03 10:00:00',0.00,2,1),(160,'P1G691383','Controlador de Bahía Siemens 6MD85','SIPROTEC 5 1/3x19\" 11 BI, 9 BO, display grande','Siprotec 5','PE-PICO-S01-K2124','Pasillo 1 - Anaquel 4 - Piso 2','2026-02-03 10:15:00',0.00,9,2),(161,'P1E918428','Relé Protección de Barras Siemens 7SS85','SIPROTEC 5 1/3x19\" 19 BI, 11 BO, Base + 150 puntos','Siprotec 5','PE-PICO-S01-K2124','Pasillo 1 - Anaquel 4 - Piso 3','2026-02-03 10:30:00',0.00,2,1),(162,'RST2228-BUS','Switch Bus de Proceso RUGGEDCOM RST2228','Siemens 6GK6222-6AB00-5CN0 montaje rack 19\"','Telecomunicaciones','PE-PICO-S01-K2124','Pasillo 1 - Anaquel 5 - Piso 1','2026-02-04 09:00:00',0.00,8,2),(163,'METSEION92040','Medidor Multifunción Schneider ION 9000','Medidor calidad energía clase 0.1S IEC 61850','Medidor','PE-PICO-S01-K2124','Pasillo 1 - Anaquel 5 - Piso 2','2026-02-04 09:20:00',0.00,3,1),(164,'METSEION7400','Medidor Multifunción Schneider ION 7400','Medidor panel calidad energía IEC 61850','Medidor','PE-PICO-S01-K2124','Pasillo 1 - Anaquel 5 - Piso 2','2026-02-04 09:30:00',0.00,5,1),(165,'3VA1163-3EF32','Interruptor Caja Moldeada 3VA1 63A 3P','Siemens SENTRON Frame 160 25kA@415V TM240','MCCB','PE-PICO-S01-K2124','Pasillo 2 - Anaquel 2 - Piso 1','2026-02-04 11:20:00',0.00,10,2),(166,'5SY5206-7','Interruptor Bipolar 6A DC/AC','Siemens SENTRON 440VDC / 400VAC 10kA 2 polos','MCB','PE-PICO-S01-K2124','Pasillo 2 - Anaquel 2 - Piso 2','2026-02-04 11:30:00',0.00,113,15),(167,'5ST3012','Contacto Auxiliar Mini-Interruptor 2NC','Siemens SENTRON bloque auxiliar 2NC','MCB','PE-PICO-S01-K2124','Pasillo 2 - Anaquel 2 - Piso 2','2026-02-04 11:40:00',0.00,349,35),(168,'3004362','Borne Sencillo Phoenix UK 5 N (57A)','Borne de paso gris 800V conexión por tornillo','Bornera','PE-PICO-S01-K2124','Pasillo 3 - Gaveta 05 - Nivel 1','2026-02-05 15:30:00',0.00,3362,200),(169,'3004032','Borne Seccionable Cuchilla UK5-MTK P/P','Borne de prueba 16A 500V circuitos de control','Bornera','PE-PICO-S01-K2124','Pasillo 3 - Gaveta 05 - Nivel 2','2026-02-05 16:00:00',0.00,4385,250),(170,'SIVACON-8MF1','Celda 8MF1 Control y Protección IP55','Siemens SIVACON 8MF1 800x800x2200mm con abatible rack','Tableros','PE-PICO-S03-K2124','Zona Patio Techado - Bahía D1','2026-02-08 09:00:00',0.00,4,1),(171,'SIVACON-S8','Celda Tipo S8 Servicios Auxiliares VAC IP55','Siemens SIVACON S8 800x800x2200mm','Tableros','PE-PICO-S03-K2124','Zona Patio Techado - Bahía D2','2026-02-08 09:15:00',0.00,2,1),(172,'SPO-MK2C','MKs Dos Cuerpos Autosoportado','SPO 1900x1600x800mm tablero patio','Tableros','PE-PICO-S03-K2124','Zona Patio Techado - Bahía D3','2026-02-08 09:30:00',0.00,2,1),(173,'P1M211697','Merging Unit Siemens 6MU85 (2/3x19\")','SIPROTEC 5 71 BI, 25 BO, Base + 30 puntos','Siprotec 5','PE-PICO-S03-K2124','Pasillo 1 - Anaquel 5 - Piso 1','2026-02-09 09:30:00',0.00,4,1),(174,'P1C972857','Relé Diferencial Línea Siemens 7SL87','SIPROTEC 5 1/2x19\" 11 BI, 9 BO, FO 100km','Siprotec 5','PE-PICO-S03-K2124','Pasillo 1 - Anaquel 5 - Piso 1','2026-02-09 10:10:00',0.00,2,1),(175,'P1C972932','Relé Diferencial Línea Siemens 7SL87 Large','SIPROTEC 5 1/2x19\" display grande, FO 100km','Siprotec 5','PE-PICO-S03-K2124','Pasillo 1 - Anaquel 5 - Piso 2','2026-02-09 10:25:00',0.00,2,1),(176,'P1G691383','Controlador de Bahía Siemens 6MD85','SIPROTEC 5 1/3x19\" 11 BI, 9 BO, display grande','Siprotec 5','PE-PICO-S03-K2124','Pasillo 1 - Anaquel 5 - Piso 2','2026-02-09 10:40:00',0.00,2,1),(177,'RST2228-BUS','Switch Bus de Proceso RUGGEDCOM RST2228','Siemens 6GK6222-6AB00-5CN0 montaje rack 19\"','Telecomunicaciones','PE-PICO-S03-K2124','Pasillo 1 - Anaquel 6 - Piso 1','2026-02-10 09:00:00',0.00,2,1),(178,'METSEION92040','Medidor Multifunción Schneider ION 9000','Medidor calidad energía clase 0.1S IEC 61850','Medidor','PE-PICO-S03-K2124','Pasillo 1 - Anaquel 6 - Piso 2','2026-02-10 09:20:00',0.00,2,1),(179,'5SY5206-7','Interruptor Bipolar 6A DC/AC','Siemens SENTRON 440VDC / 400VAC 10kA 2 polos','MCB','PE-PICO-S03-K2124','Pasillo 2 - Anaquel 3 - Piso 1','2026-02-10 11:00:00',0.00,33,5),(180,'3004362','Borne Sencillo Phoenix UK 5 N (57A)','Borne de paso gris 800V conexión por tornillo','Bornera','PE-PICO-S03-K2124','Pasillo 3 - Gaveta 06 - Nivel 1','2026-02-10 14:30:00',0.00,1040,100),(181,'3004032','Borne Seccionable Cuchilla UK5-MTK P/P','Borne de prueba 16A 500V circuitos de control','Bornera','PE-PICO-S03-K2124','Pasillo 3 - Gaveta 06 - Nivel 2','2026-02-10 14:45:00',0.00,1072,100),(182,'SIVACON-8MF1','Celda 8MF1 Control y Protección IP55','Siemens SIVACON 8MF1 800x800x2200mm con abatible rack','Tableros','PE-PICO-S04-K2124','Zona Patio Techado - Bahía E1','2026-02-12 08:30:00',0.00,2,1),(183,'SIVACON-S8','Celda Tipo S8 Servicios Auxiliares VAC IP55','Siemens SIVACON S8 800x800x2200mm','Tableros','PE-PICO-S04-K2124','Zona Patio Techado - Bahía E2','2026-02-12 08:40:00',0.00,1,1),(184,'6MF2805-0AA00','Controlador Subestación SICAM A8000 CP8050','CPU Siemens SICAM A8000 automatización S.E. Paita','Siprotec 5','PE-PICO-S04-K2124','Pasillo 1 - Anaquel 6 - Piso 1','2026-02-12 09:15:00',0.00,1,1),(185,'P1G947633','Controlador de Bahía Siemens 6MD85','SIPROTEC 5 2/3x19\" 75 BI, 17 BO, ETH-BB-2FO','Siprotec 5','PE-PICO-S04-K2124','Pasillo 1 - Anaquel 6 - Piso 2','2026-02-12 09:30:00',0.00,1,1),(186,'P1C1017717','Relé Diferencial Línea Siemens 7SL86','SIPROTEC 5 1/3x19\" 11 BI, 9 BO, FO 24km','Siprotec 5','PE-PICO-S04-K2124','Pasillo 1 - Anaquel 6 - Piso 2','2026-02-12 09:45:00',0.00,1,1),(187,'P1C1017748','Relé Diferencial Línea Siemens 7SL86 Large','SIPROTEC 5 1/2x19\" 55 BI, 14 BO, display grande','Siprotec 5','PE-PICO-S04-K2124','Pasillo 1 - Anaquel 6 - Piso 2','2026-02-12 10:00:00',0.00,1,1),(188,'RST2228-AB','Switch Sistema A/B RUGGEDCOM RST2228','Siemens 6GK6222-6AB00-5CN0 montaje rack 19\"','Telecomunicaciones','PE-PICO-S04-K2124','Pasillo 1 - Anaquel 7 - Piso 1','2026-02-12 10:15:00',0.00,2,1),(189,'METSEION7400','Medidor Multifunción Schneider ION 7400','Medidor panel calidad energía IEC 61850','Medidor','PE-PICO-S04-K2124','Pasillo 1 - Anaquel 7 - Piso 2','2026-02-12 10:30:00',0.00,1,1),(190,'3004362','Borne Sencillo Phoenix UK 5 N (57A)','Borne de paso gris 800V conexión por tornillo','Bornera','PE-PICO-S04-K2124','Pasillo 3 - Gaveta 07 - Nivel 1','2026-02-13 11:00:00',0.00,293,50),(191,'3004032','Borne Seccionable Cuchilla UK5-MTK P/P','Borne de prueba 16A 500V circuitos de control','Bornera','PE-PICO-S04-K2124','Pasillo 3 - Gaveta 07 - Nivel 2','2026-02-13 11:15:00',0.00,217,40),(193,'SIVACON-8MF1','Celda 8MF1 Control y Protección IP55','Siemens SIVACON 8MF1 800x800x2200mm con abatible rack','Tableros','PE-PICO-S05-K2124','Zona Patio Techado - Bahía F1','2026-02-15 08:30:00',0.00,2,1),(194,'SIVACON-S8','Celda Tipo S8 Servicios Auxiliares VAC IP55','Siemens SIVACON S8 800x800x2200mm','Tableros','PE-PICO-S05-K2124','Zona Patio Techado - Bahía F2','2026-02-15 08:40:00',0.00,1,1),(195,'SPO-CAJA-CTVT','Caja Agrupamiento Corrientes y Tensiones','SPO 750x500x250mm acero galvanizado','Tableros','PE-PICO-S05-K2124','Zona Patio Techado - Bahía F3','2026-02-15 08:50:00',0.00,2,1),(196,'6MF2805-0AA00','Controlador Subestación SICAM A8000 CP8050','CPU Siemens SICAM A8000 Paita Industrial','Siprotec 5','PE-PICO-S05-K2124','Pasillo 1 - Anaquel 7 - Piso 1','2026-02-15 09:00:00',0.00,1,1),(197,'P1G947633','Controlador de Bahía Siemens 6MD85','SIPROTEC 5 2/3x19\" 75 BI, 17 BO, ETH-BB-2FO','Siprotec 5','PE-PICO-S05-K2124','Pasillo 1 - Anaquel 7 - Piso 2','2026-02-15 09:10:00',0.00,1,1),(198,'P1C1017717','Relé Diferencial Línea Siemens 7SL86','SIPROTEC 5 1/3x19\" 11 BI, 9 BO, FO 24km','Siprotec 5','PE-PICO-S05-K2124','Pasillo 1 - Anaquel 7 - Piso 2','2026-02-15 09:20:00',0.00,1,1),(199,'P1C1017748','Relé Diferencial Línea Siemens 7SL86 Large','SIPROTEC 5 1/2x19\" 55 BI, 14 BO, display grande','Siprotec 5','PE-PICO-S05-K2124','Pasillo 1 - Anaquel 7 - Piso 2','2026-02-15 09:30:00',0.00,1,1),(200,'RST2228-AB','Switch Sistema A/B RUGGEDCOM RST2228','Siemens 6GK6222-6AB00-5CN0 montaje rack 19\"','Telecomunicaciones','PE-PICO-S05-K2124','Pasillo 1 - Anaquel 8 - Piso 1','2026-02-15 09:45:00',0.00,2,1),(201,'METSEION7400','Medidor Multifunción Schneider ION 7400','Medidor panel calidad energía IEC 61850','Medidor','PE-PICO-S05-K2124','Pasillo 1 - Anaquel 8 - Piso 2','2026-02-15 10:00:00',0.00,1,1),(202,'3004362','Borne Sencillo Phoenix UK 5 N (57A)','Borne de paso gris 800V conexión por tornillo','Bornera','PE-PICO-S05-K2124','Pasillo 3 - Gaveta 08 - Nivel 1','2026-02-16 10:30:00',0.00,243,50),(203,'3004032','Borne Seccionable Cuchilla UK5-MTK P/P','Borne de prueba 16A 500V circuitos de control','Bornera','PE-PICO-S05-K2124','Pasillo 3 - Gaveta 08 - Nivel 2','2026-02-16 10:45:00',0.00,197,40),(204,'P1M211543','Merging Unit Siemens 6MU85 (Base)','Siemens SIPROTEC 5 5/6x19\", 79 BI, 28 BO, 8 CT, 8 VT, IO202/207/208/230','Siprotec 5','PE-BETA-S03-K2124','Pasillo 8 - Anaquel 5 - Piso 1','2026-11-20 03:38:38',0.00,10,1);
/*!40000 ALTER TABLE `material` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merma_devolucion`
--

DROP TABLE IF EXISTS `merma_devolucion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `merma_devolucion` (
  `id_merma` bigint NOT NULL AUTO_INCREMENT,
  `id_proyecto` bigint NOT NULL,
  `id_material` bigint NOT NULL,
  `id_usuario_registra` bigint NOT NULL,
  `id_usuario_validador` bigint DEFAULT NULL,
  `tipo` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cantidad` int NOT NULL,
  `motivo` text COLLATE utf8mb4_unicode_ci,
  `estado` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'PENDIENTE',
  `fecha` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_merma`),
  KEY `id_proyecto` (`id_proyecto`),
  KEY `id_material` (`id_material`),
  KEY `id_usuario_registra` (`id_usuario_registra`),
  KEY `id_usuario_validador` (`id_usuario_validador`),
  CONSTRAINT `merma_devolucion_ibfk_1` FOREIGN KEY (`id_proyecto`) REFERENCES `proyecto` (`id_proyecto`),
  CONSTRAINT `merma_devolucion_ibfk_2` FOREIGN KEY (`id_material`) REFERENCES `material` (`id_material`),
  CONSTRAINT `merma_devolucion_ibfk_3` FOREIGN KEY (`id_usuario_registra`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `merma_devolucion_ibfk_4` FOREIGN KEY (`id_usuario_validador`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `merma_devolucion_chk_1` CHECK ((`tipo` in (_utf8mb4'DEVOLUCION',_utf8mb4'MERMA'))),
  CONSTRAINT `merma_devolucion_chk_2` CHECK ((`cantidad` > 0)),
  CONSTRAINT `merma_devolucion_chk_3` CHECK ((`estado` in (_utf8mb4'PENDIENTE',_utf8mb4'REINTEGRADO',_utf8mb4'MERMADO')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merma_devolucion`
--

LOCK TABLES `merma_devolucion` WRITE;
/*!40000 ALTER TABLE `merma_devolucion` DISABLE KEYS */;
/*!40000 ALTER TABLE `merma_devolucion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orden_compra`
--

DROP TABLE IF EXISTS `orden_compra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orden_compra` (
  `id_orden_compra` bigint NOT NULL AUTO_INCREMENT,
  `codigo` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `proveedor` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_emision` date NOT NULL,
  `estado` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'ABIERTA',
  PRIMARY KEY (`id_orden_compra`),
  UNIQUE KEY `codigo` (`codigo`),
  CONSTRAINT `orden_compra_chk_1` CHECK ((`estado` in (_utf8mb4'ABIERTA',_utf8mb4'CERRADA')))
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orden_compra`
--

LOCK TABLES `orden_compra` WRITE;
/*!40000 ALTER TABLE `orden_compra` DISABLE KEYS */;
INSERT INTO `orden_compra` VALUES (4,'OC-SAYA-2026-001','Siemens S.A.C. (Smart Infrastructure)','2026-01-15','ABIERTA'),(5,'OC-SAYA-2026-002','Phoenix Contact Perú S.A.C.','2026-01-20','ABIERTA'),(6,'OC-SAYA-2026-003','Schneider Electric Perú S.A.','2026-02-01','ABIERTA'),(7,'OC-BETA-S03','Siemens S.A.C. (Smart Infrastructure)','2026-11-19','ABIERTA');
/*!40000 ALTER TABLE `orden_compra` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parametro_prueba`
--

DROP TABLE IF EXISTS `parametro_prueba`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parametro_prueba` (
  `id_parametro` bigint NOT NULL AUTO_INCREMENT,
  `tipo_prueba` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `unidad` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `valor_minimo` decimal(10,3) NOT NULL,
  `valor_maximo` decimal(10,3) NOT NULL,
  `norma_referencia` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_parametro`),
  CONSTRAINT `chk_valores` CHECK ((`valor_maximo` >= `valor_minimo`))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parametro_prueba`
--

LOCK TABLES `parametro_prueba` WRITE;
/*!40000 ALTER TABLE `parametro_prueba` DISABLE KEYS */;
INSERT INTO `parametro_prueba` VALUES (1,'METALMECANICA','Checklist',1.000,1.000,'IEC 61439-1 (Inspección Física)'),(2,'ELECTRICA','Checklist',1.000,1.000,'IEC 60255-27 (Cableado y Continuidad)');
/*!40000 ALTER TABLE `parametro_prueba` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permiso`
--

DROP TABLE IF EXISTS `permiso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permiso` (
  `id_permiso` bigint NOT NULL AUTO_INCREMENT,
  `codigo` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id_permiso`),
  UNIQUE KEY `codigo` (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permiso`
--

LOCK TABLES `permiso` WRITE;
/*!40000 ALTER TABLE `permiso` DISABLE KEYS */;
/*!40000 ALTER TABLE `permiso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `proyecto`
--

DROP TABLE IF EXISTS `proyecto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `proyecto` (
  `id_proyecto` bigint NOT NULL AUTO_INCREMENT,
  `codigo` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `cliente` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_inicio` date DEFAULT NULL,
  `fecha_entrega_estimada` date DEFAULT NULL,
  `responsable` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'EN_PROCESO',
  PRIMARY KEY (`id_proyecto`),
  UNIQUE KEY `codigo` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proyecto`
--

LOCK TABLES `proyecto` WRITE;
/*!40000 ALTER TABLE `proyecto` DISABLE KEYS */;
INSERT INTO `proyecto` VALUES (1,'PE-SAYA-S01-K2124','ISA CTM - Ampliación S.E. San José 500kV (Tableros C&P y SS.AA.)','2026-02-09','2026-04-30','Carlos Mendoza (Jefe Producción)','EN_PROCESO'),(2,'PE-SAYA-S02-K7730','ISA CTM - Ampliación S.E. Yarabamba 500kV (Tableros Telecomunicaciones)','2026-02-24','2026-05-15','Juan Pérez (Técnico Líder)','EN_PROCESO'),(3,'PE-BETA-S01-K2124','ISA CTM - S.E. Mayo 220 kV (Enlace Belaunde Terry - Tarapoto Norte)','2025-11-28','2026-04-30','Carlos Mendoza (Jefe Producción)','EN_PROCESO'),(4,'PE-BETA-S03-K2124','ISA CTM - S.E. Belaunde Terry 220 kV','2025-12-02','2026-05-15','Juan Pérez (Técnico Líder)','EN_PROCESO'),(5,'PE-PICO-S01-K2124','ISA CTM - S.E. Colán 220/60 kV (Enlace Miguel Grau - Nueva Colán)','2026-01-14','2026-06-10','Carlos Mendoza (Jefe Producción)','EN_PROCESO'),(6,'PE-PICO-S03-K2124','ISA CTM - S.E. Miguel Grau 220 kV','2025-12-30','2026-05-25','Supervisor Operaciones - SPO','EN_PROCESO'),(7,'PE-PICO-S04-K2124','ISA CTM - S.E. Paita 60 kV','2026-01-08','2026-06-20','Juan Pérez (Técnico Líder)','EN_PROCESO'),(8,'PE-PICO-S05-K2124','ISA CTM - S.E. Paita Industrial 60 kV','2026-01-08','2026-06-25','Supervisor Operaciones - SPO','EN_PROCESO');
/*!40000 ALTER TABLE `proyecto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prueba_calidad`
--

DROP TABLE IF EXISTS `prueba_calidad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prueba_calidad` (
  `id_prueba` bigint NOT NULL AUTO_INCREMENT,
  `id_proyecto` bigint NOT NULL,
  `id_parametro` bigint NOT NULL,
  `id_usuario_inspector` bigint NOT NULL,
  `valor_medido` decimal(10,3) NOT NULL,
  `resultado` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `observacion` text COLLATE utf8mb4_unicode_ci,
  `fecha_prueba` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_prueba`),
  KEY `id_proyecto` (`id_proyecto`),
  KEY `id_parametro` (`id_parametro`),
  KEY `id_usuario_inspector` (`id_usuario_inspector`),
  CONSTRAINT `prueba_calidad_ibfk_1` FOREIGN KEY (`id_proyecto`) REFERENCES `proyecto` (`id_proyecto`),
  CONSTRAINT `prueba_calidad_ibfk_2` FOREIGN KEY (`id_parametro`) REFERENCES `parametro_prueba` (`id_parametro`),
  CONSTRAINT `prueba_calidad_ibfk_3` FOREIGN KEY (`id_usuario_inspector`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `prueba_calidad_chk_1` CHECK ((`resultado` in (_utf8mb4'CONFORME',_utf8mb4'NO_CONFORME')))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prueba_calidad`
--

LOCK TABLES `prueba_calidad` WRITE;
/*!40000 ALTER TABLE `prueba_calidad` DISABLE KEYS */;
INSERT INTO `prueba_calidad` VALUES (1,2,2,5,1.000,'CONFORME','Checklist 100% SÍ. Cable: 2.5 mm  | Tierra: Estándar. Obs: SIN OBSERVACIONES','2026-11-19 06:28:03'),(2,1,2,5,1.000,'CONFORME','Checklist 100% SÍ. Cable: 2.5 mm  | Tierra: Estándar. ','2026-11-19 06:28:42'),(3,1,1,5,0.000,'NO_CONFORME','[Tablero: =B00+W00_A — TABLERO CONTROLADOR SISTEMA A] Checklist 100% SÍ. Obs: sin observaciones','2026-11-19 06:29:16');
/*!40000 ALTER TABLE `prueba_calidad` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rol`
--

DROP TABLE IF EXISTS `rol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol` (
  `id_rol` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id_rol`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rol`
--

LOCK TABLES `rol` WRITE;
/*!40000 ALTER TABLE `rol` DISABLE KEYS */;
INSERT INTO `rol` VALUES (1,'ADMIN','Administrador del sistema y aprobador de presupuestos'),(2,'JEFE_PRODUCCION','Jefe de producción y creador de proyectos/solicitudes'),(3,'ALMACENERO','Encargado de inventario, ingresos y despacho de kits'),(4,'OPERARIO_ENSAMBLE','Técnico de taller que recibe kits y devuelve sobrantes'),(5,'INSPECTOR','Inspector de control de calidad y emisión de certificados');
/*!40000 ALTER TABLE `rol` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rol_permiso`
--

DROP TABLE IF EXISTS `rol_permiso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol_permiso` (
  `id_rol` bigint NOT NULL,
  `id_permiso` bigint NOT NULL,
  PRIMARY KEY (`id_rol`,`id_permiso`),
  KEY `id_permiso` (`id_permiso`),
  CONSTRAINT `rol_permiso_ibfk_1` FOREIGN KEY (`id_rol`) REFERENCES `rol` (`id_rol`) ON DELETE CASCADE,
  CONSTRAINT `rol_permiso_ibfk_2` FOREIGN KEY (`id_permiso`) REFERENCES `permiso` (`id_permiso`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rol_permiso`
--

LOCK TABLES `rol_permiso` WRITE;
/*!40000 ALTER TABLE `rol_permiso` DISABLE KEYS */;
/*!40000 ALTER TABLE `rol_permiso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `solicitud`
--

DROP TABLE IF EXISTS `solicitud`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `solicitud` (
  `id_solicitud` bigint NOT NULL AUTO_INCREMENT,
  `id_proyecto` bigint NOT NULL,
  `id_usuario_solicitante` bigint NOT NULL,
  `id_usuario_aprobador` bigint DEFAULT NULL,
  `fecha_solicitud` datetime DEFAULT CURRENT_TIMESTAMP,
  `costo_total` decimal(12,2) DEFAULT '0.00',
  `estado` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT 'BORRADOR',
  PRIMARY KEY (`id_solicitud`),
  KEY `id_proyecto` (`id_proyecto`),
  KEY `id_usuario_solicitante` (`id_usuario_solicitante`),
  KEY `id_usuario_aprobador` (`id_usuario_aprobador`),
  CONSTRAINT `solicitud_ibfk_1` FOREIGN KEY (`id_proyecto`) REFERENCES `proyecto` (`id_proyecto`),
  CONSTRAINT `solicitud_ibfk_2` FOREIGN KEY (`id_usuario_solicitante`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `solicitud_ibfk_3` FOREIGN KEY (`id_usuario_aprobador`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `solicitud_chk_1` CHECK ((`estado` in (_utf8mb4'BORRADOR',_utf8mb4'RETENIDA',_utf8mb4'RECHAZADA',_utf8mb4'LISTA_DESPACHO',_utf8mb4'DESPACHADA')))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `solicitud`
--

LOCK TABLES `solicitud` WRITE;
/*!40000 ALTER TABLE `solicitud` DISABLE KEYS */;
INSERT INTO `solicitud` VALUES (1,7,2,NULL,'2026-11-20 03:50:19',0.00,'DESPACHADA'),(2,8,2,NULL,'2026-11-20 03:53:34',0.00,'DESPACHADA');
/*!40000 ALTER TABLE `solicitud` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trazabilidad`
--

DROP TABLE IF EXISTS `trazabilidad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `trazabilidad` (
  `id_trazabilidad` bigint NOT NULL AUTO_INCREMENT,
  `id_material` bigint NOT NULL,
  `id_usuario` bigint NOT NULL,
  `id_proyecto` bigint DEFAULT NULL,
  `id_orden_compra` bigint DEFAULT NULL,
  `tipo_movimiento` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cantidad` int NOT NULL,
  `fecha_movimiento` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_trazabilidad`),
  KEY `id_material` (`id_material`),
  KEY `id_usuario` (`id_usuario`),
  KEY `id_proyecto` (`id_proyecto`),
  KEY `id_orden_compra` (`id_orden_compra`),
  CONSTRAINT `trazabilidad_ibfk_1` FOREIGN KEY (`id_material`) REFERENCES `material` (`id_material`),
  CONSTRAINT `trazabilidad_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `trazabilidad_ibfk_3` FOREIGN KEY (`id_proyecto`) REFERENCES `proyecto` (`id_proyecto`),
  CONSTRAINT `trazabilidad_ibfk_4` FOREIGN KEY (`id_orden_compra`) REFERENCES `orden_compra` (`id_orden_compra`),
  CONSTRAINT `trazabilidad_chk_1` CHECK ((`tipo_movimiento` in (_utf8mb4'ENTRADA',_utf8mb4'SALIDA',_utf8mb4'DEVOLUCION',_utf8mb4'MERMA'))),
  CONSTRAINT `trazabilidad_chk_2` CHECK ((`cantidad` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trazabilidad`
--

LOCK TABLES `trazabilidad` WRITE;
/*!40000 ALTER TABLE `trazabilidad` DISABLE KEYS */;
INSERT INTO `trazabilidad` VALUES (1,204,3,NULL,7,'ENTRADA',10,'2026-11-20 03:38:38'),(2,57,4,7,NULL,'SALIDA',3,'2026-11-20 04:02:34'),(3,203,4,8,NULL,'SALIDA',20,'2026-11-20 04:02:39'),(4,202,4,8,NULL,'SALIDA',50,'2026-11-20 04:02:39');
/*!40000 ALTER TABLE `trazabilidad` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id_usuario` bigint NOT NULL AUTO_INCREMENT,
  `id_rol` bigint NOT NULL,
  `nombre_completo` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `correo` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `activo` tinyint(1) DEFAULT '1',
  `ultimo_acceso` datetime DEFAULT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `correo` (`correo`),
  KEY `id_rol` (`id_rol`),
  CONSTRAINT `usuario_ibfk_1` FOREIGN KEY (`id_rol`) REFERENCES `rol` (`id_rol`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,1,'Walter Izquierdo (Admin)','admin@smartpoweron.pe','$2a$10$uTmaDb6KOadaIG/XZJifw.mJt/gXxKXTigaa0HBfkcKdbHYOgn28u',1,NULL),(2,2,'Carlos Mendoza (Producción)','produccion@smartpoweron.pe','$2a$10$uTmaDb6KOadaIG/XZJifw.mJt/gXxKXTigaa0HBfkcKdbHYOgn28u',1,NULL),(3,3,'Luis Torres (Almacén)','almacen@smartpoweron.pe','$2a$10$uTmaDb6KOadaIG/XZJifw.mJt/gXxKXTigaa0HBfkcKdbHYOgn28u',1,NULL),(4,4,'Jorge Quispe (Operario)','operario@smartpoweron.pe','$2a$10$uTmaDb6KOadaIG/XZJifw.mJt/gXxKXTigaa0HBfkcKdbHYOgn28u',1,NULL),(5,5,'Ana Rojas (Calidad)','calidad@smartpoweron.pe','$2a$10$uTmaDb6KOadaIG/XZJifw.mJt/gXxKXTigaa0HBfkcKdbHYOgn28u',1,NULL),(6,4,'Usuario Inactivo Prueba','inactivo@smartpoweron.pe','$2a$10$uTmaDb6KOadaIG/XZJifw.mJt/gXxKXTigaa0HBfkcKdbHYOgn28u',0,NULL);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-19 23:23:28
