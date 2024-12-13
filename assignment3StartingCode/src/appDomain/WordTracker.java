/*********************************************************
** Program Name:                              Word Tracker
** Authors:										Team Purah
** Created:                              December 10, 2024
**
*********************************************************/

package appDomain;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import implementations.BSTree;
import implementations.BSTreeNode;

/**
 * WordTracker that tracks words, their occurrences in files, and generates
 * reports.
 */
public class WordTracker
{

	private static final String REPOSITORY_FILE = "repository.ser";

	private BSTree<WordMetaWrapper> wordTree;

	public WordTracker()
	{
		wordTree = new BSTree<>();
	}

	public static void main( String[] args )
	{
		if ( args.length < 2 )
		{
			System.err.println( "Usage: java -jar WordTracker.jar <input.txt> -pf/-pl/-po [-f <output.txt>]" );
			System.exit( 1 );
		}
		String inputFilePath = args[0];
		String command = args[1];
		String outputFilePath = ( args.length == 4 && args[2].equals( "-f" ) ) ? args[3] : null;

		// Load or initialize the BSTree
		WordTracker wordTracker = new WordTracker();
		BSTree<WordMetaWrapper> wordTree = wordTracker.loadTree();

		// Process the input file
		wordTracker.processFile( inputFilePath, wordTree );

		// Save the updated tree
		wordTracker.saveTree( wordTree );

		// Generate the report
		wordTracker.generateReport( wordTree, command, outputFilePath );
	}

	private BSTree<WordMetaWrapper> loadTree()
	{
		File repository = new File( REPOSITORY_FILE );

		if ( repository.exists() )
		{
			try ( ObjectInputStream ois = new ObjectInputStream( new FileInputStream( repository ) ) )
			{
				return (BSTree<WordMetaWrapper>) ois.readObject();
			} catch ( IOException | ClassNotFoundException e )
			{
				System.err.println( "Failed to load repository. Starting with a new tree." );
			}
		}
		return new BSTree<>();
	}

	private void saveTree( BSTree<WordMetaWrapper> wordTree )
	{
		try ( ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream( REPOSITORY_FILE ) ) )
		{
			oos.writeObject( wordTree );
		} catch ( IOException e )
		{
			System.err.println( "Failed to save repository: " + e.getMessage() );
		}
	}

	private void processFile( String filePath, BSTree<WordMetaWrapper> wordTree )
	{
		try ( BufferedReader reader = new BufferedReader( new FileReader( filePath ) ) )
		{
			String line;
			int lineNumber = 1;

			while ( ( line = reader.readLine() ) != null )
			{
				String[] words = line.split( "\\W+" );
				for ( String word : words )
				{
					if ( !word.isEmpty() )
					{
						addWordToTree( word.toLowerCase(), filePath, lineNumber, wordTree );
					}
				}
				lineNumber++;
			}
			reader.close();
		} catch ( IOException e )
		{
			System.err.println( "Error reading file: " + e.getMessage() );
		}
	}

	private void addWordToTree( String word, String filePath, int lineNumber, BSTree<WordMetaWrapper> wordTree )
	{
		BSTreeNode<WordMetaWrapper> node = wordTree.search( new WordMetaWrapper( word, null ) );

		WordMetaWrapper wrapper;
		if ( node == null )
		{
			wrapper = new WordMetaWrapper( word, new WordMeta() );
			wordTree.add( wrapper );
		}
		else
		{
			wrapper = node.getElement();
		}

		wrapper.addOccurrence( filePath, lineNumber );
	}

	private void generateReport( BSTree<WordMetaWrapper> wordTree, String command, String outputFilePath )
	{
		List<String> reportLines = new ArrayList<>();

		utilities.Iterator<WordMetaWrapper> iterator = wordTree.inorderIterator();
		while ( iterator.hasNext() )
		{
			WordMetaWrapper wrapper = iterator.next();
			WordMeta meta = wrapper.getMeta();
			reportLines.add( formatReportLine( wrapper.getWord(), meta, command ) );
		}

		try ( PrintWriter writer = (outputFilePath == null) ? new PrintWriter(System.out) : new PrintWriter(new FileWriter(outputFilePath)) )
		{
			for ( String line : reportLines )
			{
				writer.println( line );
			}
			if ( outputFilePath != null )
			{
				System.out.println("\nOutput file generated.\n");
			}
		} catch ( IOException e )
		{
			System.err.println( "Failed to write report: " + e.getMessage() );
		}
	}

	private String formatReportLine( String word, WordMeta meta, String command )
	{
		StringBuilder line = new StringBuilder( word );
		line.insert( 0, "Key : ===" ).append( "===" );

		if ( command.equals( "-po" ) )
		{
			final int[] count = { 0 };
			meta.getOccurrences().forEach( ( file, lines ) ->
			{
				count[0] += lines.size();
			} );
			line.append( "   number of entries: " ).append( count[0] );
		}

		int ocrCount = 0;
		meta.getOccurrences().forEach( ( file, lines ) ->
		{
			line.append( "  found in file:  " ).append( file );
			if ( !command.equals( "-pf" ) )
			{
				line.append( "  on lines: " ).append(
						String.join( ", ", lines.stream().map( String::valueOf ).collect( Collectors.toList() ) ) );
			}
		} );
		return line.toString();
	}
}

// Metadata class for word occurrences
class WordMeta implements Serializable
{
	private static final long serialVersionUID = 1L;
	private Map<String, Set<Integer>> occurrences;

	public WordMeta()
	{
		occurrences = new HashMap<>();
	}

	public void addOccurrence( String file, int line )
	{
		occurrences.computeIfAbsent( file, k -> new HashSet<>() ).add( line );
	}

	public Map<String, Set<Integer>> getOccurrences()
	{
		return occurrences;
	}

	public int getFrequency( String file )
	{
		return occurrences.getOrDefault( file, Collections.emptySet() ).size();
	}
}

class WordMetaWrapper implements Serializable, Comparable<WordMetaWrapper>
{
	private static final long serialVersionUID = 1L;
	private String word;
	private WordMeta meta;

	public WordMetaWrapper( String word, WordMeta meta )
	{
		this.word = word;
		this.meta = meta;
	}

	public String getWord()
	{
		return word;
	}

	public WordMeta getMeta()
	{
		return meta;
	}

	public void addOccurrence( String file, int line )
	{
		meta.addOccurrence( file, line );
	}

	@Override
	public String toString()
	{
		return word + ": " + meta.getOccurrences();
	}

	@Override
	public int compareTo( WordMetaWrapper other )
	{
		return this.word.compareTo( other.word );
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;
		if ( obj == null || getClass() != obj.getClass() )
			return false;
		WordMetaWrapper that = (WordMetaWrapper) obj;
		return word.equals( that.word );
	}

	@Override
	public int hashCode()
	{
		return Objects.hash( word );
	}
}
