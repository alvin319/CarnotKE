# connOracleEE_native = connectTo 'jdbc:oracle:thin:@sayonara.microlab.cs.utexas.edu:1521:orcl' 'C##cs329e_UTEid' 'orcl_UTEid' 'native_mode' nodebug
connOracleEE = connectTo 'jdbc:oracle:thin:@sayonara.microlab.cs.utexas.edu:1521:orcl' 'C##cs329e_UTEid' 'orcl_UTEid' 'rdf_mode' 'A0' nodebug

# connOracleRDFNoSQL = connectTo 'OracleNoSQL' 'C##cs329e_UTEid' 'orcl_UTEid' 'rdf_mode' 'A0'

# A heterogeneous set of inserts is used for testing Oracle EE
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7369, ENAME : 'SMITH', JOB : 'CLERK', MGR : 7902, HIREDATE : '17-DEC-80', SAL : 800, COMM : 0, DEPTNO : 20})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7499, ENAME : 'ALLEN', JOB : 'SALESMAN', MGR : 7698, HIREDATE : '20-FEB-81', SAL : 1600, COMM : 300, DEPTNO : 30})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7521, ENAME : 'WARD', JOB : 'SALESMAN', MGR : 7698, HIREDATE : '22-FEB-81', SAL : 1250, COMM : 500, DEPTNO : 30})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7566, ENAME : 'JONES', JOB : 'MANAGER', MGR : 7839, HIREDATE : '02-APR-81', SAL : 2975, COMM : 0, DEPTNO : 20})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7654, ENAME : 'MARTIN', JOB : 'SALESMAN', MGR : 7698, HIREDATE : '28-SEP-81', SAL : 1250, COMM : 1400, DEPTNO : 30})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7698, ENAME : 'BLAKE', JOB : 'MANAGER', MGR : 7839, HIREDATE : '01-MAY-81', SAL : 2850, COMM : 0, DEPTNO : 30})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7782, ENAME : 'CLARK', JOB : 'MANAGER', MGR : 7839, HIREDATE : '09-JUN-81', SAL : 2450, COMM : 0, DEPTNO : 10})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7788, ENAME : 'SCOTT', JOB : 'ANALYST', MGR : 7566, HIREDATE : '09-DEC-82', SAL : 3000, COMM : 0, DEPTNO : 20})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7839, ENAME : 'KING', JOB : 'PRESIDENT', MGR : 0, HIREDATE : '17-NOV-81', SAL : 5000, COMM : 0, DEPTNO : 10})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7844, ENAME : 'TURNER', JOB : 'SALESMAN', MGR : 7698, HIREDATE : '08-SEP-81', SAL : 1500, COMM : 0, DEPTNO : 30})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7876, ENAME : 'ADAMS', JOB : 'CLERK', MGR : 7788, HIREDATE : '12-JAN-83', SAL : 1100, COMM : 0, DEPTNO : 20})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7900, ENAME : 'JAMES', JOB : 'CLERK', MGR : 7698, HIREDATE : '03-DEC-81', SAL : 950, COMM : 0, DEPTNO : 30})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7902, ENAME : 'FORD', JOB : 'ANALYST', MGR : 7566, HIREDATE : '03-DEC-81', SAL : 3000, COMM : 0, DEPTNO : 20})"
Neo4j on connOracleEE "CREATE (:emp { EMPNO : 7934, ENAME : 'MILLER', JOB : 'CLERK', MGR : 7782, HIREDATE : '23-JAN-82', SAL : 1300, COMM : 0, DEPTNO : 50})"

SQL on connOracleEE "INSERT INTO DEPT (DEPTNO, DNAME, LOC) VALUES (10, 'ACCOUNTING', 'NEW YORK');"
SQL on connOracleEE "INSERT INTO DEPT (DEPTNO, DNAME, LOC) VALUES (20, 'RESEARCH',   'DALLAS');"
SQL on connOracleEE "INSERT INTO DEPT (DEPTNO, DNAME, LOC) VALUES (30, 'SALES',      'CHICAGO');"
SQL on connOracleEE "INSERT INTO DEPT (DEPTNO, DNAME, LOC) VALUES (40, 'OPERATIONS', 'BOSTON');"

Neo4j on connOracleEE "MATCH (a:emp),(b:dept) WHERE a.deptno = 10 AND b.deptno = 10 CREATE (a)<-[:employees]-(b)"
Neo4j on connOracleEE "MATCH (a:emp),(b:dept) WHERE a.deptno = 20 AND b.deptno = 20 CREATE (a)<-[:employees]-(b)"
Neo4j on connOracleEE "MATCH (a:emp),(b:dept) WHERE a.deptno = 30 AND b.deptno = 30 CREATE (a)<-[:employees]-(b)"
Neo4j on connOracleEE "MATCH (a:emp),(b:dept) WHERE a.deptno = 40 AND b.deptno = 40 CREATE (a)<-[:employees]-(b)"

Neo4j on connOracleEE "MATCH (a:emp),(b:dept) WHERE a.deptno = 10 AND b.deptno = 10 CREATE (a)-[:dept]->(b)"
Neo4j on connOracleEE "MATCH (a:emp),(b:dept) WHERE a.deptno = 20 AND b.deptno = 20 CREATE (a)-[:dept]->(b)"
Neo4j on connOracleEE "MATCH (a:emp),(b:dept) WHERE a.deptno = 30 AND b.deptno = 30 CREATE (a)-[:dept]->(b)"
Neo4j on connOracleEE "MATCH (a:emp),(b:dept) WHERE a.deptno = 40 AND b.deptno = 40 CREATE (a)-[:dept]->(b)"


print "Finished loading the NoSQL Database"
