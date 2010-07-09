import os, glob

# Use: Set the root directory to the directory on the server with the photos 
# Run the script with python
# Run the generated sql script as such psql -f migrationscript.sql dbname

rootdir='/home/ACCT04/romanoa1/photomig'
w = open('./migrationscript.sql', 'w')
def get_blob(value):
	return value and ''.join(['\\\\%03o' % ord(c) for c in value])

for path, dirs, files in os.walk(rootdir):
	for file in files:
		print "Found image: " + file
		with open(path + '/' + file, 'r') as f:
			w.write('insert into Image (version, imageIdentifier, imageBlob) values (0, \'' + file + '\', E\''+get_blob(f.read())+'\');') 
