package com.sandy.codegen.config;

import java.util.List ;
import java.util.Map ;
import java.util.regex.Matcher ;
import java.util.regex.Pattern ;

import org.apache.log4j.Logger ;

import com.sandy.common.util.StringUtil ;

import ognl.Ognl ;

public class ConfigUtils {

    static final Logger log = Logger.getLogger( ConfigUtils.class ) ;
    
    public static final String INDENT1 = "   " ;
    public static final String INDENT2 = INDENT1 + INDENT1 ;
    public static final String INDENT3 = INDENT2 + INDENT1 ;
    
    private static final String CFG_VAR_PATTERN = "\\$\\{([^\\{]*)}+" ;

    public static String getFormattedMapContents( String name, 
                                                  Map<String, Object> map, 
                                                  String indent ) {
        
        if( map == null || map.isEmpty() ) return "" ;
        
        StringBuilder builder = new StringBuilder() ;
        builder.append( indent + name + " -> {\n" ) ;
        for( String key : map.keySet() ) {
            builder.append( indent + INDENT1 + "[" + key + "] : " )
                   .append( map.get( key ) )
                   .append( "\n" ) ;
        }
        builder.append( indent + "}\n" ) ;
        return builder.toString() ;
    }

    public static String getFormattedListContents( List<? extends Object> list,
                                                   String indent ) {
        
        if( list == null || list.isEmpty() ) return "" ;
        
        StringBuilder builder = new StringBuilder() ;
        for( Object listItem : list ) {
            builder.append( "\n" + indent + "- " + listItem ) ;
        }
        return builder.toString() ;
    }

    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public static void enrichMap( Map map, 
                                  CodeGenConfig config ) 
        throws Exception {
        
        if( map == null ) return ;
        
        for( Object key : map.keySet() ) {
            Object value = map.get( key ) ;
            if( value instanceof String ) {
                String enrichedStrVal = enrichString( (String)value, config ) ;
                map.put( key, enrichedStrVal ) ;
            }
            else if( value instanceof List ) {
                List list = ( List )value ;
                enrichList( list, config ) ;
            }
            else if( value instanceof Map ) {
                enrichMap( ( Map )value, config ) ;
            }
            else {
                log.debug( value.getClass().getName() );
            }
        }
    }
    
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public static void enrichList( List list,
                                   CodeGenConfig config ) 
        throws Exception {
        
        if( list == null ) return ;
        
        for( int i=0; i<list.size(); i++ ) {
            Object value = list.get( i ) ;
            if( value instanceof String ) {
                String enrichedStrVal = enrichString( (String)value, config ) ;
                list.set( i, enrichedStrVal ) ;
            }
            else if( value instanceof List<?> ) {
                enrichList( ( List )value, config ) ;
            }
        }
    }
    
    public static String enrichString( String input, CodeGenConfig config ) 
        throws Exception {
        
        if( StringUtil.isEmptyOrNull( input ) ) return input ;
        
        StringBuilder outputBuffer = new StringBuilder() ;
        
        Pattern r = Pattern.compile( CFG_VAR_PATTERN, Pattern.DOTALL ) ;
        Matcher m = r.matcher( input ) ;
        
        int lastEndMarker = 0 ;
        
        while( m.find() ) {
            int start = m.start() ;
            int end   = m.end() ;
            
            String processedString = processVar( input.substring( start, end ), config ) ;
            if( processedString != null ) {
                outputBuffer.append( input.substring( lastEndMarker, start ) ) ;
                outputBuffer.append( processedString ) ;
                lastEndMarker = end ;
            }
        }
        
        outputBuffer.append( input.substring(lastEndMarker, input.length() ) ) ;
        return outputBuffer.toString() ;

    }

    private static String processVar( String input, CodeGenConfig config ) 
            throws Exception {
        
        String varName = input.substring( 2, input.length()-1 ) ;
        try {
            Object varValue = Ognl.getValue( varName, config ) ;
            if( varValue == null ) {
                throw new RuntimeException( "Could not find config variable - " + varName ) ;
            }
            return varValue.toString() ;
        }
        catch( Exception e ) {
            throw new Exception( "Error processing Ognl var " + varName, e ) ;
        }
    }
}
