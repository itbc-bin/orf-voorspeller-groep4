import re
import warnings
import sys
import subprocess
import platform

import mysql.connector
from Bio import BiopythonWarning
from Bio import SearchIO
from Bio.Blast import NCBIWWW


def main():
    header = sys.argv[1]
    sequence = sys.argv[2]
    # blastp(sequence, 'results.xml')
    # result_list = read_xml_file('results.xml')
    # insert_database_sequence(sequence, header)
    # insert_database_protein(result_list)
    call_java(header)


def blastp(seq, output_file_name):
    """Blasting the fasta files
    :param output_file_name:  name of the XML file that will be created
    :param seq:               protein sequence
    :return: an XML file with the BLAST output
    """
    result_handle = NCBIWWW.qblast('blastp', 'nr', seq,
                                   word_size=6, gapcosts='11 1',
                                   expect=0.0001, format_type='XML')
    out_handle = open(output_file_name, 'w')
    # replace apostrophe and '>' in XML file, because that cannot be
    # inserted in the database
    handle = result_handle.read().replace('&apos;', '').replace('&gt;', '>')
    out_handle.write(handle)
    out_handle.close()


def connection_database():
    """Make connection to the database
    :return: The connection
    """
    connection = mysql.connector.connect(
        host='hannl-hlo-bioinformatica-mysqlsrv.mysql.database.azure.com',
        user='ounhs@hannl-hlo-bioinformatica-mysqlsrv',
        database='ounhs',
        password='LM6lx70EFxVb')
    return connection


def read_xml_file(file_name):
    """Parsing the XML file, and getting values. And calculating values
    based on the values in the XML file
    :param file_name: Name of fasta file that should be used
    :return: a list with all the results from the XML file
    """
    warnings.simplefilter('ignore', BiopythonWarning)
    blast_results = SearchIO.parse(file_name, 'blast-xml')
    # os.remove('results.xml')
    result_list = []
    counter = 0
    sequence_id = 1

    # amout of hits that should be saved into the database
    saving_hits = 10

    for result in blast_results:
        for res in range(len(result)):
            if counter < saving_hits:
                # getting all of the different values
                accession = result[res].accession
                definition = result[res].description
                id_ = result[res][0].query_id
                bitscore = result[res][0].bitscore
                evalue = result[res][0].evalue
                ident_num = result[res][0].ident_num
                pos_num = result[res][0].pos_num
                gap_num = result[res][0].gap_num
                query_range = [result[res][0].query_range]
                align_len = result[res][0].hit_span
                ident_perc = round(ident_num / align_len * 100, 2)
                query_cov = round(([query[1] - query[0] for query in
                                    query_range][0]) / 301 * 100, 2)
                # appending all the values to a list, so it can be used later
                result_list.append(
                    [sequence_id, accession, id_, definition, bitscore,
                     evalue, ident_num, pos_num, gap_num, ident_perc,
                     query_cov])

                counter += 1
        sequence_id += 1
        counter = 0
    return result_list


def insert_database_sequence(sequence, header):
    """Inserting the header, sequence and score into the database
    """

    connection = connection_database()
    cursor = connection.cursor()
    cursor.execute("select count(*) from sequence")
    result = cursor.fetchone()
    amount_res = [amount for amount in result][0]
    counter = amount_res + 1

    # inserting the read 1 (forward) sequuences into the database
    cursor.execute(
        "insert into sequence(seq_id, sequence, header)"
        " values ('{}', '{}', '{}')".format(counter, sequence, header))
    connection.commit()


def insert_database_protein(result_list):
    """Inserting all the results into the database
    :param result_list: List with all the results
    :return: An updated database
    """

    # definition of protein is untill opening bracket [
    definition = r'.*\['

    connection = connection_database()
    cursor = connection.cursor()

    # getting current length of database
    cursor.execute("select count(*) from protein")
    result = cursor.fetchone()
    amount_res = [amount for amount in result][0]
    counter = amount_res + 1

    for result in result_list:
        match_def = re.search(definition, result[3])
        if match_def:
            # delete the bracket from the protein
            protein = match_def.group().replace('[', '')

            # inserting the protein values into the database
            cursor.execute(
                "insert into protein(name_id, defenition, "
                "accession) values ('{}', '{}', '{}')".format(counter,
                                                              protein,
                                                              result[1]))

            # inserting all the attributes into the database
            cursor.execute(
                "insert into protein_attribute(protein_id, seq_id, "
                "name_id, ident_num, pos_num, gap_num, e_value, "
                "bit_score, ident_perc, query_cov) values ('{}', '{}',"
                " '{}', '{}', '{}', '{}', '{}', '{}', '{}', '{}')".format(
                    counter, result[0], counter, result[6], result[7],
                    result[8], result[5], result[4], result[9], result[10]))

            connection.commit()
            # add 1 to the counter, so the next result will have a unique
            # primary key in the database
            counter += 1
    connection.close()


def call_java(header):
    if platform.system() == 'Windows':
        cmd = ['cmd', '/c', 'cmdFiles\\Windows\\runJava.bat', header]
    else:
        cmd = ['java', '-cp', 'jarFiles/mysql-connector-java-8.0.19.jar', 'src/BLASTResultsGUI.java', header]
        subprocess.check_output(cmd)


if __name__ == '__main__':
    main()
