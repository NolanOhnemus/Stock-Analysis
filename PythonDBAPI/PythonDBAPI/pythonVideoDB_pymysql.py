#!/usr/bin/python

# As of 9/29/2018 not compatible with mysql 8.0*
import pymysql

dict = True

def listMovies():
    # sudo pip install PyMySQL
    # conda install PyMySQL
    # http://nbviewer.ipython.org/gist/slarson/6745890

    # Open database connection
    if dict == False:
        connection = pymysql.connect("localhost", "root", "admin", "video_db")
    else:
        connection = pymysql.connect(host='localhost',
                                     user='root',
                                     password='admin123',
                                     db='video_db',
                                     charset='utf8mb4',
                                     cursorclass=pymysql.cursors.DictCursor)

    # prepare a cursor object using cursor() method
    cursor = connection.cursor()
    # cursor = connection.cursor(pymysql.cursors.DictCursor)

    # Prepare SQL query to read records from the database
    sql = "select vr.title, vr.director, vr.category from Video_Recordings vr order by vr.title limit 10"

    # sql = "SELECT * FROM Video_Recordings \
    #        WHERE title > '%s'"
    try:
        # Execute the SQL command
        cursor.execute(sql)
        # Fetch all the rows in a list of lists.
        results = cursor.fetchall()
        print(type(results))
        for row in results:
            print(type(row))
            print(row)

            # standard cursor
            if dict == False:
                # array
                title = row[0]
                director = row[1]
                category = row[2]

            # dict cursor
            else:
                # array
                title = row.get('title')
                director = row.get('director')
                category = row.get('category')

            # Now print fetched result
            print ("title=%s,director=%s,category=%s" % \
                   (title, director, category))

    except:
        print ("Error: unable to fecth data")

    # disconnect from server
    connection.close()


if __name__ == '__main__':
    print ("List Movies")
    listMovies()

#listMovies()
