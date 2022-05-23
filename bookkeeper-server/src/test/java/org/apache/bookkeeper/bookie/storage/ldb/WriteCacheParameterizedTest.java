package org.apache.bookkeeper.bookie.storage.ldb;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class WriteCacheParameterizedTest {
    private boolean expected;

    private long ledgerId;
    private long entryId;
    private ByteBuf entry;
    private WriteCache cache = null;
    private boolean existsMaxSegmentSize; // è un valore che in alcuni test è disponibile, altri no.

//costruttore
    public WriteCacheParameterizedTest(boolean expected, long ledgerId, long entryId, ByteBuf entry, boolean existsMaxSegmentSize){
        configure(expected,ledgerId,entryId,entry,existsMaxSegmentSize);
    }

    public void configure(boolean expected, long ledgerId, long entryId, ByteBuf entry,boolean existsMaxSegmentSize)
    {
        this.expected = expected;
        this.ledgerId = ledgerId;
        this.entryId = entryId;
        this.entry = entry;
        this.existsMaxSegmentSize = existsMaxSegmentSize; //serve nel setup per definire new writeCache
    }

    @Parameterized.Parameters
    public static Collection<?> getParameter() {

        return Arrays.asList(new Object[][] {
                //expected    ledgerId    entryId                        entry                               maxSegmentSize
                {false,          0,          0,      null,                                                   false}, //null entry
                {false,         -1,          1,      UnpooledByteBufAllocator.DEFAULT.buffer(1024),          false}, //ledgerId <0
                {true,           0,          0,      UnpooledByteBufAllocator.DEFAULT.buffer(1024),          false},
                {false,          0,          0,      UnpooledByteBufAllocator.DEFAULT.buffer(10*1024+1),     true }, //cache full
                {false,          0,         -1,      UnpooledByteBufAllocator.DEFAULT.buffer(1024),          false } //entryId < 0


        });
    }

    @Before
    public void setup(){
        if (existsMaxSegmentSize)
        {
            cache = new WriteCache(UnpooledByteBufAllocator.DEFAULT,10*1024,16*1024);
        }
        else{
            cache = new WriteCache(UnpooledByteBufAllocator.DEFAULT,10*1024);
        }

        if (entry != null){
            entry.writerIndex(entry.capacity());
        }
    }

    @Test
    public void TestWriteCache(){
        boolean actual;
        try {
            cache.put(ledgerId,entryId,entry);
            actual = cache.get(ledgerId,entryId).equals(entry);
        } catch (Exception e)
        {
            actual = false;
        }
        Assert.assertEquals(expected,actual);
    }

    @After
    public void teardown()
    {
        if (entry != null){
                    entry.release();
        }
        cache.clear();
        cache.close();

    }



}
