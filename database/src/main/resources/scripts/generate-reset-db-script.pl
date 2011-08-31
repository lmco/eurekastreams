#!/usr/bin/perl

if($#ARGV != 1)
{
    print STDERR "ERROR:\n2 required params:\n";
    print "\t1: source directory for eureka - should contain the 'database' directory\n";
    print "\t2: owner of the database\n";
    exit 1;
}

if(! -d "$ARGV[0]/database")
{
    print STDERR "ERROR:\n\tInvalid source directory - couldn't find 'database' subdirectory\n";
    exit 1;
}

$dir = "/Users/caldwelw/Documents/Development/Eureka/src/eurekastreams/database/src/main/resources";
my @dirs = ("${dir}/schema", "${dir}/migration/0/7", "${dir}/migration/0/8", "${dir}/migration/0/9", "${dir}/migration/1/0", "${dir}/migration/1/1", "${dir}/migration/1/5");

print "DROP DATABASE eurekastreams;\n";
print "CREATE DATABASE eurekastreams WITH OWNER=" . $ARGV[1] . ";\n";

for my $d(@dirs)
{
    opendir(DIR, $d) or die $!;
    my @files = ();
    while(my $file = readdir(DIR)) 
    {
	next if ($file =~ m/^\./);
	push(@files, $d . '/' . $file);
    }
    closedir(DIR);
    @files = sort{$a cmp $b} @files;

    foreach $file (@files)
    {
	open(SMALLFILE, "<$file");
	print <SMALLFILE>;
	close(SMALLFILE);
    }
} 


