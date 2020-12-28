import sqlite3
from faker import Faker
import random
import argparse

p = argparse.ArgumentParser()
p.add_argument('g', type=int, default=20, help='groups', nargs='?')
p.add_argument('p', type=int, default=500, help='persons', nargs='?')
p.add_argument('d', type=int, default=15, help='departments', nargs='?')
args = p.parse_args()
f = Faker(['ru-RU', 'en_US'])
conn = sqlite3.connect('frontier.db')
conn.row_factory = lambda cursor, row: row[0]
c = conn.cursor()
tables = c.execute("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name").fetchall()
if len(tables) <= 1:
    with open('scheme.sql', 'r') as file:
        scheme = file.read().split(';')
        for req in scheme: c.execute(req)
for i in range(args.g): c.execute('INSERT INTO groups (name) VALUES (?)', [f'group{i}'])
for i in range(args.p): c.execute('INSERT INTO person (name, age) VALUES (?, ?)', [f.name(), random.randint(18, 30)])
dept_insert_st = 'INSERT INTO dept (name) VALUES (?)'
for i in range(args.d): c.execute(dept_insert_st, [f.address()])
c.execute(dept_insert_st, ['ПИиКТ'])
group_ids = c.execute('SELECT number FROM groups').fetchall()
person_ids = c.execute('SELECT id FROM person').fetchall()
dept_ids = c.execute('SELECT id FROM dept').fetchall()
half_persons = list(person_ids)
random.shuffle(half_persons)
half_persons = half_persons[:len(person_ids) // 2]
for id in half_persons:
    c.execute('INSERT INTO student (group_num, person_id, dept_id) VALUES (?, ?, ?)',
              [random.choice(group_ids), id, random.choice(dept_ids)])
fourth_persons = list(person_ids)
random.shuffle(fourth_persons)
fourth_persons = fourth_persons[:len(person_ids) // 4]
for id in fourth_persons:
    c.execute('INSERT INTO employee (person_id, dept_id) VALUES (?, ?)',
              [id, random.choice(dept_ids)])
conn.commit()
c.close()
