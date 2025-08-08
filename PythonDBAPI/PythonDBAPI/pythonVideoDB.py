#!/usr/bin/python

'''
Jay Urbain
9/29/2018

Connect and query video db with MySQL Connector

Reference:
https://dev.mysql.com/doc/connector-python/en/connector-python-introduction.html

sudo pip install mysql-connector-python
or
conda install -c anaconda mysql-connector-python
'''

# Open database connection
import mysql.connector

def listMovies():


    connection = mysql.connector.connect(user='root', password='admin123', host='127.0.0.1', database='video_db')
    # prepare a cursor object using cursor() method
    cursor = connection.cursor()
    # cursor = connection.cursor(pymysql.cursors.DictCursor)

    # Prepare SQL query to read records from the database
    sql = ("select vr.title, vr.director, vr.category from Video_Recordings vr order by vr.title limit 10")

    # sql = "SELECT * FROM Video_Recordings \
    #        WHERE title > '%s'"
    try:
        # Execute the SQL command
        cursor.execute(sql)

        for r in cursor:
            print(r[0], r[1], r[1])

            # standard cursor
            title = r[0]
            director = r[1]
            category = r[2]

    except:
        print ("Error: unable to fecth data")

    # disconnect from server
    connection.close()


if __name__ == '__main__':
    print ("List Movies")
    listMovies()

#listMovies()
