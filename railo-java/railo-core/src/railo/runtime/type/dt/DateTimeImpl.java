package railo.runtime.type.dt;


import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import railo.commons.date.DateTimeUtil;
import railo.commons.lang.CFTypes;
import railo.commons.lang.SizeOf;
import railo.runtime.PageContext;
import railo.runtime.config.Config;
import railo.runtime.dump.DumpData;
import railo.runtime.dump.DumpProperties;
import railo.runtime.dump.DumpTable;
import railo.runtime.dump.SimpleDumpData;
import railo.runtime.engine.ThreadLocalPageContext;
import railo.runtime.exp.ExpressionException;
import railo.runtime.exp.PageException;
import railo.runtime.op.Operator;
import railo.runtime.reflection.Reflector;
import railo.runtime.type.Collection.Key;
import railo.runtime.type.util.MemberUtil;
import railo.runtime.type.Objects;
import railo.runtime.type.SimpleValue;
import railo.runtime.type.Sizeable;
import railo.runtime.type.Struct;

/**
 * Printable and Castable DateTime Object
 */
public final class DateTimeImpl extends DateTime implements SimpleValue,Sizeable,Objects {
	
	public DateTimeImpl(PageContext pc) {
		this(pc,System.currentTimeMillis(),true);
	}
	
	public DateTimeImpl(Config config) {
		this(config,System.currentTimeMillis(),true);
	}
	
	public DateTimeImpl() {
		this(System.currentTimeMillis(),true);
	}
	
	public DateTimeImpl(PageContext pc, long utcTime, boolean doOffset) {
		super(doOffset?addOffset(ThreadLocalPageContext.getConfig(pc), utcTime):utcTime);
	}

	public DateTimeImpl(Config config, long utcTime, boolean doOffset) {
		super(doOffset?addOffset(ThreadLocalPageContext.getConfig(config), utcTime):utcTime);
	}
	
	public DateTimeImpl(long utcTime, boolean doOffset) {
		super(doOffset?addOffset(ThreadLocalPageContext.getConfig(), utcTime):utcTime);
	}
	
	/*public DateTimeImpl(Config config, long utcTime) {
		super(addOffset(ThreadLocalPageContext.getConfig(config),utcTime));
	}*/

	public DateTimeImpl(Date date) {
		this(date.getTime(),false);
	}
	
	
	public DateTimeImpl(Calendar calendar) {
		super(calendar.getTimeInMillis());
		//this.timezone=ThreadLocalPageContext.getTimeZone(calendar.getTimeZone());
	}

	public static long addOffset(Config config, long utcTime) {
		if(config!=null) return utcTime+config.getTimeServerOffset();
		return utcTime;
	}
	
	
	/**
	 * @see railo.runtime.dump.Dumpable#toDumpData(railo.runtime.PageContext, int)
	 */
	public DumpData toDumpData(PageContext pageContext, int maxlevel, DumpProperties dp) {
		String str=castToString(pageContext.getTimeZone());
        DumpTable table=new DumpTable("date","#ff6600","#ffcc99","#000000");
        if(dp.getMetainfo())
        	table.appendRow(1, new SimpleDumpData("Date Time ("+pageContext.getTimeZone().getID()+")"));
        else
        	table.appendRow(1, new SimpleDumpData("Date Time"));
        table.appendRow(0, new SimpleDumpData(str));
        return table;
	}

	/**
	 * @see railo.runtime.op.Castable#castToString()
	 */
	public String castToString() {
		return castToString((TimeZone)null);
    }


    /**
     * @see railo.runtime.op.Castable#castToString(java.lang.String)
     */
    public String castToString(String defaultValue) {
        return castToString((TimeZone)null);
    }
    
	public String castToString(TimeZone tz) {// MUST move to DateTimeUtil
		return DateTimeUtil.getInstance().toString(this,tz);
		
	}
	
	/**
	 * @see railo.runtime.op.Castable#castToBooleanValue()
	 */
	public boolean castToBooleanValue() throws ExpressionException {
        return DateTimeUtil.getInstance().toBooleanValue(this);
	}
    
    /**
     * @see railo.runtime.op.Castable#castToBoolean(java.lang.Boolean)
     */
    public Boolean castToBoolean(Boolean defaultValue) {
        return defaultValue;
    }

	/**
	 * @see railo.runtime.op.Castable#castToDoubleValue()
	 */
	public double castToDoubleValue() {
	    return toDoubleValue();
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDoubleValue(double)
     */
    public double castToDoubleValue(double defaultValue) {
        return toDoubleValue();
    }

	/**
	 * @see railo.runtime.op.Castable#castToDateTime()
	 */
	public DateTime castToDateTime() {
		return this;
	}
    
    /**
     * @see railo.runtime.op.Castable#castToDateTime(railo.runtime.type.dt.DateTime)
     */
    public DateTime castToDateTime(DateTime defaultValue) {
        return this;
    }
	
	/**
     * @see railo.runtime.type.dt.DateTime#toDoubleValue()
     */
	public double toDoubleValue() {
	    return DateTimeUtil.getInstance().toDoubleValue(this);
	}


	/**
	 * @see railo.runtime.op.Castable#compare(boolean)
	 */
	public int compareTo(boolean b) {
		return Operator.compare(castToDoubleValue(), b?1D:0D);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(railo.runtime.type.dt.DateTime)
	 */
	public int compareTo(DateTime dt) throws PageException {
		return Operator.compare((java.util.Date)this, (java.util.Date)dt);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(double)
	 */
	public int compareTo(double d) throws PageException {
		return Operator.compare(castToDoubleValue(), d);
	}

	/**
	 * @see railo.runtime.op.Castable#compareTo(java.lang.String)
	 */
	public int compareTo(String str) {
		return Operator.compare(castToString(), str);
	}
	
	public String toString() {
		return castToString();
        /*synchronized (javaFormatter) {
        	javaFormatter.setTimeZone(timezone);
            return javaFormatter.format(this);
        }*/
	}

	@Override
	public long sizeOf() {
		return SizeOf.LONG_SIZE+SizeOf.REF_SIZE;
	}


	@Override
	public Object get(PageContext pc, Key key, Object defaultValue) {
		return Reflector.getField(this, key.getString(),defaultValue);
	}

	@Override
	public Object get(PageContext pc, Key key) throws PageException {
		return Reflector.getField(this, key.getString());
	}

	@Override
	public Object set(PageContext pc, Key propertyName, Object value) throws PageException {
		return Reflector.setField(this, propertyName.getString(),value);
	}

	@Override
	public Object setEL(PageContext pc, Key propertyName, Object value) {
		try {
			return Reflector.setField(this, propertyName.getString(),value);
		} catch (PageException e) {
			return value;
		}
	}

	@Override
	public Object call(PageContext pc, Key methodName, Object[] args) throws PageException {
		return MemberUtil.call(pc, this, methodName, args, CFTypes.TYPE_DATETIME, "datetime");
	}

	@Override
	public Object callWithNamedValues(PageContext pc, Key methodName, Struct args) throws PageException {
		return MemberUtil.callWithNamedValues(pc,this,methodName,args, CFTypes.TYPE_DATETIME, "datetime");
	}
	
}