-- MySQL dump 10.13  Distrib 8.0.43, for Linux (x86_64)
--
-- Host: localhost    Database: MerControle
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `funcionarios`
--

DROP TABLE IF EXISTS `funcionarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `funcionarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `cargo` varchar(50) NOT NULL,
  `salario` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `funcionarios`
--

LOCK TABLES `funcionarios` WRITE;
/*!40000 ALTER TABLE `funcionarios` DISABLE KEYS */;
INSERT INTO `funcionarios` VALUES (1,'Geovana','Gerente',3000.00);
/*!40000 ALTER TABLE `funcionarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `produtos`
--

DROP TABLE IF EXISTS `produtos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `produtos` (
  `id` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(10) NOT NULL,
  `nome` varchar(100) NOT NULL,
  `preco_compra` decimal(10,2) NOT NULL,
  `preco_venda` decimal(10,2) NOT NULL,
  `estoque` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `codigo` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produtos`
--

LOCK TABLES `produtos` WRITE;
/*!40000 ALTER TABLE `produtos` DISABLE KEYS */;
INSERT INTO `produtos` VALUES (3,'5LZCF38T','miójos',2.00,3.00,0),(5,'FEI01','Feijão 1kg',8.00,10.00,0),(6,'MAC05','Macarrão 500g',4.00,6.00,6),(7,'IDTQKT4F','Batata',2.00,3.00,4),(11,'GFQ1MB7E','teste',5.00,2.50,9);
/*!40000 ALTER TABLE `produtos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transacaoFinanceira`
--

DROP TABLE IF EXISTS `transacaoFinanceira`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transacaoFinanceira` (
  `id` int NOT NULL AUTO_INCREMENT,
  `data` date NOT NULL,
  `valor` decimal(10,2) NOT NULL,
  `categoria` enum('ENTRADA','SAIDA') NOT NULL,
  `usuario_id` int DEFAULT NULL,
  `descricao` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `transacaoFinanceira_ibfk_1` (`usuario_id`),
  CONSTRAINT `transacaoFinanceira_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transacaoFinanceira`
--

LOCK TABLES `transacaoFinanceira` WRITE;
/*!40000 ALTER TABLE `transacaoFinanceira` DISABLE KEYS */;
INSERT INTO `transacaoFinanceira` VALUES (1,'2025-08-20',6.00,'ENTRADA',4,NULL),(2,'2025-08-20',24.00,'ENTRADA',4,NULL),(3,'2025-08-20',3.00,'ENTRADA',4,NULL),(4,'2025-08-20',6.00,'ENTRADA',4,NULL),(5,'2025-08-20',6.00,'ENTRADA',4,NULL),(6,'2025-08-21',50.00,'ENTRADA',NULL,NULL),(7,'2025-08-21',25.00,'ENTRADA',NULL,NULL),(8,'2025-08-21',50.00,'ENTRADA',NULL,NULL),(9,'2025-08-21',38.00,'ENTRADA',4,NULL),(10,'2025-08-21',62.00,'ENTRADA',4,NULL),(11,'2025-08-21',250.00,'SAIDA',NULL,'conta de luz'),(12,'2025-08-21',38.00,'ENTRADA',NULL,NULL),(13,'2025-08-21',40.00,'ENTRADA',NULL,NULL),(14,'2025-08-21',42.00,'ENTRADA',NULL,NULL),(15,'2025-08-21',0.00,'ENTRADA',NULL,NULL),(16,'2025-08-21',500.00,'SAIDA',4,'Aluguel'),(17,'2025-08-21',40.00,'ENTRADA',4,NULL),(18,'2025-08-21',70.00,'ENTRADA',4,NULL),(19,'2025-08-21',11.50,'ENTRADA',4,NULL);
/*!40000 ALTER TABLE `transacaoFinanceira` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `login` varchar(50) NOT NULL,
  `senha` varchar(255) NOT NULL,
  `tipo` enum('Administrador','Funcionario') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `login` (`login`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (4,'Admin','admini','240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','Administrador'),(12,'mariane','mari','a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3','Funcionario');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vendaItens`
--

DROP TABLE IF EXISTS `vendaItens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vendaItens` (
  `id` int NOT NULL AUTO_INCREMENT,
  `vendaId` int NOT NULL,
  `produtoId` int DEFAULT NULL,
  `descricao` varchar(200) NOT NULL,
  `precoUnitario` decimal(10,2) NOT NULL,
  `quantidade` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_vendaItens_venda` (`vendaId`),
  KEY `fk_vendaItens_produto` (`produtoId`),
  CONSTRAINT `fk_vendaItens_produto` FOREIGN KEY (`produtoId`) REFERENCES `produtos` (`id`),
  CONSTRAINT `fk_vendaItens_venda` FOREIGN KEY (`vendaId`) REFERENCES `vendas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vendaItens`
--

LOCK TABLES `vendaItens` WRITE;
/*!40000 ALTER TABLE `vendaItens` DISABLE KEYS */;
INSERT INTO `vendaItens` VALUES (1,13,NULL,'4 - Arroz 5kg',30.00,1),(2,13,5,'5 - Feijão 1kg',10.00,1),(3,14,5,'5 - Feijão 1kg',10.00,7),(4,16,7,'7 - Batata',3.00,1),(5,16,6,'6 - Macarrão 500g',6.00,1),(6,16,11,'11 - teste',2.50,1);
/*!40000 ALTER TABLE `vendaItens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vendas`
--

DROP TABLE IF EXISTS `vendas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vendas` (
  `id` int NOT NULL AUTO_INCREMENT,
  `data` timestamp NULL DEFAULT NULL,
  `valorTotal` decimal(10,2) NOT NULL,
  `usuarioId` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `usuarioId` (`usuarioId`),
  CONSTRAINT `vendas_ibfk_1` FOREIGN KEY (`usuarioId`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vendas`
--

LOCK TABLES `vendas` WRITE;
/*!40000 ALTER TABLE `vendas` DISABLE KEYS */;
INSERT INTO `vendas` VALUES (4,'2025-08-21 03:00:00',50.00,NULL),(5,'2025-08-21 03:00:00',25.00,NULL),(6,'2025-08-21 03:00:00',50.00,NULL),(7,'2025-08-21 03:00:00',38.00,4),(8,'2025-08-21 03:00:00',62.00,4),(9,'2025-08-21 03:00:00',38.00,NULL),(10,'2025-08-21 14:57:58',40.00,NULL),(11,'2025-08-21 17:17:48',42.00,NULL),(12,'2025-08-21 17:18:10',0.00,NULL),(13,'2025-08-21 18:33:29',40.00,4),(14,'2025-08-21 18:45:27',70.00,4),(16,'2025-08-21 19:20:00',11.50,4);
/*!40000 ALTER TABLE `vendas` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-21 20:13:40
