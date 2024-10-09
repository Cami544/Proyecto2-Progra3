CREATE DATABASE Pos;

use Pos;

create table Categoria (
       id  varchar(10)  not null,
       nombre varchar(30) not null,
       Primary Key (id)
     );

create table Producto (
       codigo  varchar(10)  not null,
       descripcion varchar(30) not null,
	   unidadMedida  varchar(20),
	   precioUnitario  float,
	   categoria varchar(10),
       Primary Key (codigo)
     );
-- create Table Cliente(
--     id varchar(100) not null,
--     nombre varchar(45) not null,
--     telefono varchar(45) not null,
--     email varchar(45) not null,
--     nombre float,
--     PRIMARY KEY (id)
-- );
--
-- create Table Cajero(
--     id varchar(100) not null,
--     nombre varchar(45) not null,
-- );

ALTER TABLE Producto ADD Foreign Key (categoria) REFERENCES Categoria(id);

insert into Categoria (id ,nombre) values('001','Aguas');
insert into Categoria (id ,nombre) values('002','Dulces');
insert into Categoria (id ,nombre) values('003','Aceites');
insert into Categoria (id ,nombre) values('004','Vinos');