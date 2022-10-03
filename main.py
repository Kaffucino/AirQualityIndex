import pyodbc
from merC import *
from datetime import date
from datetime import datetime
import time
import pandas as pd

# open the file in the write mode

conn = pyodbc.connect('Driver={ODBC Driver 17 for SQL Server};'
                      'Server=ELMER16\SQL_SERVER;'
                      'Database=AirQualityIndex;'
                       'Trusted_Connection=yes;')

cursor=conn.cursor()

a = 1
while a > 0:
    #read current time and format it properly
    today = date.today()
    now=datetime.now()
    # dd/mm/YY
    d1 = today.strftime("%d/%m/%Y")
    # H:M:S
    t1 = now.strftime("%H:%M:%S")
    niz=readData()

    #read and insert data values for pm10 concentration
    insert_pm10 = """INSERT INTO [dbo].[VALUES_PM]
                       ([PM]
                       ,[VAL]
                       ,[TIME])
                        VALUES
                        ('10', {}, '{}')""".format(niz[0], t1 + "|" + d1)
    cursor.execute(insert_pm10 )
    cursor.commit()

    #read and insert data values for pm2_5 concentration
    insert_pm2_5 = """INSERT INTO [dbo].[VALUES_PM]
                       ([PM]
                       ,[VAL]
                       ,[TIME])
                        VALUES
                        ('2_5', {}, '{}')""".format(niz[1], t1 + "|" + d1)
    cursor.execute(insert_pm2_5 )
    cursor.commit()


# while a < 5:
#     niz = readData();
#    # niz = [1, 7, 2, 4]
#  #   print(niz)
#     command1 = 'INSERT INTO VALUES_PM'
#
#     today = date.today()
#     now=datetime.now()
#     # dd/mm/YY
#     d1 = today.strftime("%d/%m/%Y")
#     # H:M:S
#     t1 = now.strftime("%H:%M:%S")
#
#     command1 += str(' VALUES (' + str(niz[1])  +',' + d1 + ',' + t1 + ')' )
#     command1 += str(', (' + str(niz[3])  +',' + d1 + ',' + t1 + ')\n' )
#     print(command1)
#  #   time.sleep(1)
#     f.write(command1)
#     f.write('go\n')

