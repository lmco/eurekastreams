#!/usr/bin/perl

$dir = "/Volumes/2TBMain/Users/blake/Documents/Development/Eureka/workspace/eurekastreams/database/src/main/resources";
my @dirs = ("${dir}/schema", "${dir}/migration/0/7", "${dir}/migration/0/8", "${dir}/migration/0/9", "${dir}/migration/1/0", "${dir}/migration/1/1", "${dir}/migration/1/5");

open(BIGFILE, ">create_database.sql");
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
	print BIGFILE <SMALLFILE>;
	close(SMALLFILE);
    }
} 
close(BIGFILE);



