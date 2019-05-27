## API consultes de EntityManager

La interfaz EntinyManager proporciona una api para realizar consultas en sintaxis JPQL
sobre la base de datos:
~~~
TypedQuery<Persona> query = em.createQuery( "select persona from
Persona persona", Persona.class);
~~~

Nota que JPQL utiliza el nombre de las clases /entidades, no el de las tablas de la BBDD.
La consulta devuelve un conjunto o un único resultado. Ojo a lo que hay a la izquierda del
operador = en la expresión (está tipado!!):

~~~
List<Persona> resultat = query.getResultList();
Persona unica = query.getSingleResult();
~~~

Si queremos utilizar parámetros en la consulta JPQL deberemos:
- Construir la consulta
~~~
TypedQuery<Persona> query = em.createQuery("select persona from
Persona persona where nom = :nom ", Persona.class);
~~~

- Indicar el nombre y el valor de estos parámetros

```query.setParameter("nom", "Joan");```

- Ejecutar la consulta

```List<Persona> resultat = query.getResultList();```

La consulta a la BBDD puede devolver tantos resultados que comprometa el rendimiento
de nuestra app, ya que las cosas en la vida real no son como dice la leyenda "640K software
is all the memory anybody would ever need on a computer."

Para fijar cuál será el primer resultado (comenzando por 0) o cuál será el máximo de
resultados a devolver:
~~~
query.setFirstResult(int);
query.setMaxResults(int);
~~~

## JPQL - Java Persistence Query Language

Disponemos de un lenguaje de consultas para nuestra aplicación JPA muy semejante a
SQL:
Nota que JPQL utiliza el nombre de las clases /entidades, no el de las tablas de la BBDD, y
se navega por la entidades /clases mediante sus propiedades (Java):
~~~
select persona
from Persona persona
where persona.nom = 'Joana'
~~~

**LAS CONSULTAS DEVUELVEN EL TIPO DE OBJETO QUE HAYAS SOLICITADO**
~~~
Select persona
from Persona persona
~~~
retorna objetos del tipo Persona.
~~~
select persona.nom
from Persona persona
~~~
**Retorna objetos del tipo String , si nom es de tipo String.**

*Si utilizamos una función de agregación como count:*
~~~
select count(persona)
from Persona persona
~~~
retorna un único resultado de tipo Number

Podemos construir estructuras arbitrarias:
~~~
select persona.nom, persona.municipi.nom
from Persona persona
~~~
que retorna un array de objectos [String, String]

**NAVEGACIÓN POR RELACIONES**
~~~
select persona
from Persona persona
join persona.municipi municipi
where municipi.nom = 'Eivissa'
~~~
Hacer **explícita** la relación entre las tablas involucradas en la consulta hace más eficiente su
rendimiento.

**THETA STYLE JOIN**
Los joins pueden escribirse en dos estilos: **ANSI – style** o **THETA - style**, que es tal que así:
~~~
select persona.nom
from Persona persona,
Municipi municipi
where municipi.nom = persona.nom
~~~ 
El otro día hablamos en clase sobre los tipos de **joins**. Cuando haces un join entre diversas
tablas, __el tipo de join que utiliza determina las filas que aparecerán en el conjunto de
resultados.__

Aquí tenéis un repaso en SQL y PL/SQL:
[https://gtusqlplsql.wordpress.com/tag/theta-style/]
~~~
SQL PL/SQL 2620006

THETA – style | SQL PL/SQL 2620006
Posts about THETA – style written by Dr. Vaishali Parsania
~~~
**Types of Joins in Oracle**
Join is a method used to combine two or more tables, views or materialized views based
on a common condition. Sometimes it is necessary to work with multiple tables as though
they were a single entity. Then a single SQL sentence can manipulate data from all the
tables. Joins are used to achieve this. Tables are joined on columns that have the same data
type in the tables.

**WHERE**
No todo lo que permite escribir SQL en el WHERE está disponible en JPQL. Echadle un ojo
al vídeo para ver los más habitual y ampliad con la referencia mientras resolvéis los
ejercicios.
~~~
Operadores lógicos : not, and, or
Comparaciones: =, <, > , <=, >=, <>
Navegación por objetos con '.'
Literales : p.e. 'aina'
Subselects
Funciones: upper(), length(), substring() ...
~~~
## Spring Data

Es un producto para reducir el volumen de código necesario para trabajar con BBDD desde
Spring.

**REPOSITORIOS**

El siguiente código crea un repositorio completamente funcional que te permite realizar
diversos tipos de consultas sobre una tabla que almacena las personas (ojo, la entidad
persona has de **mapearla** a la BBDD).
~~~
public interface PersonaRepository extends Repository<Persona, Long> {
public List<Persona> findByNom(String nom);
}
~~~

Fíjate en que:
- No es una clase, es una **interfaz**.
- No necesita la anotación ```@Repository``` para detectarla como componente (bean).
- **Extiende** de ```Repository``` . Se **parametriza** la interfaz ```Repository``` con el **tipo de la
Entidad y del ID de la entidad /clase.** Esto es un ejemplo del **polimorfismo
paramétrico** que vimos al estudiar el primer tema del libro java dedicado a los
fundamentos de la programación orientada a objetos.


**QUERIES DERIVADAS**
Se construyen las consultas a partir de las signaturas de los métodos.
Los métodos hacen referencia al nombre de las propiedades de la clase /entidad y es
posible utilizar operadores lógicos y funciones:
~~~
public List<Persona> findByMunicipiNom(String nom);
public List<Persona> findByIdOrNom(long id, String nom);
public List<Persona> findByMunicipiNomOrderByNomDesc(String nom);
~~~
**@QUERY**
Si la consulta es muy complicada, quizás convenga escribirla mediante JPQL (dentro de
nuestro ```Repository``` ).
La consulta anterior:
~~~
public List<Persona> findByMunicipiNomOrderByNomDesc(String nom);
~~~
puede escribirse anotándola con ```@Query```
~~~
@Query("select per from Persona per where per.municipi.nom = ?1 order
by per.nom")
public List<Persona> obtePersonesPerMunicipi( String municipi);
~~~
o utilizando el paso de parámetros por nombre:
~~~
@Query("select per from Persona per where per.municipi.nom = :municipi
order by per.nom")
public List<Persona> obtePersonesPerMunicipi(@Param("municipi") String
municipi);
~~~
En este caso, se emplea la anotación ```@Param``` para indicar el nombre del parámetro en la
consulta.

## CRUD Repository
La interfaz ```CrudRepository<>``` añade los métodos que corresponden a las operaciones
CRUD. La interfaz parametriza los métodos para trabajar con el tipo de la entidad (Persona)
y el tipo de su Id (Long).
~~~
public interface PersonaRepository extends CrudRepository<Persona,
Long> {
public List<Persona> findByNom(String nom);
}
~~~

- save (entity)
- saveAll ( iterable )
- findById (id)
- count()
- delete (entity )
Recuerda que cada método funciona para el **tipo de la entidad (T) y el de sus subtipos (S):**
```<S extends T> S save(S entity);```
movito por el cual es importante reescribir el método ```equals()``` de la entidad para que sea
capaz de comparar objetos del mismo tipo y de sus subtipos.

## OneToMany
Si la relación entre dos entidades A y B es de tipo A ManyToOne B, podemos darle la vuelta
y expresar la relación de la manera B OneToMany A, __mapeando únicamente la entidad B__ (no
mapeamos la columna en A) con la anotación ```@OneToMany``` e indicando mediante
```@JoinColumn``` __el nombre de la columna de la entidad A__.
Ejemplo: "muchas personas pertenecen a un municipio" => el Join se realiza con la columna
PER_MUNID de la **entidad Persona.**
~~~
@Entity
@Table(name="T_MUNICIPIS")
public class Municipi {
...

@OneToMany
@JoinColumn(name="PER_MUNID")
private Set<Persona> habitants = new HashSet<>();
...
}
~~~
__Las operaciones para eliminar o añadir personas a un municipio se realizarán desde la
entidad Municipi__, no desde Persona. ¡OJO!

## Relaciones bidirecionales
Entonces, ¿en qué entidad debemos expresar la relación?
Es posible elegir entre @ManyToOne o @OneToMany en función de:
- ¿Qué elemento es el que posee de manera más natural la relación?
- ¿Desde qué lado se recorre más veces?
Si no quieres decidir, puedes indicar que ambas entidades se conozcan la una a la otra ;)
El lado que posee la anotación ```@ManyToOne``` es el **owning side** y es el que __lee y modifica la
BBDD__:
~~~
@Entity
@Table(name="T_PERSONES")
public class Persona {
...
@ManyToOne
@JoinColumn(name="PER_MUNID")
private Municipi municipi;
...
}
~~~

El lado ```@OneToMany``` es la **relación inversa.** Con ```mappedBy``` indicamos el nombre de la
propiedad de la entidad **Owning** de esta relación (municipi). Este lado __lee pero NO modifica
la BBDD.__
~~~
@Entity
@Table(name="T_MUNICIPIS")
public class Municipi {
...
@OneToMany(mappedBy="municipi")
private Set<Persona> habitants;
...
}
~~~
