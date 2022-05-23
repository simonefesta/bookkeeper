package org.apache.bookkeeper.bookie.storage.ldb;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
public class GetLastEntryWriteCacheTest {


    private boolean expected;

    private long ledgerId;

    private ByteBuf entry = Unpooled.wrappedBuffer(new byte[1024]);
    private ByteBuf lastEntry = Unpooled.wrappedBuffer(new byte[2048]);
    private WriteCache cache = null;

    //costruttore
    public GetLastEntryWriteCacheTest(boolean expected, long ledgerId){
        configure(expected,ledgerId);
    }

    private void configure(boolean expected,long ledgerId){
        this.expected = expected;
        this.ledgerId = ledgerId;
    }

    @Parameterized.Parameters
    public static Collection<?> getTestParameters() {
        return Arrays.asList(new Object[][]{
                // expected   ledgerID
                {false,      -1             }, //put di -1 fails
                {true,        0             },  //ci metto io varie entry

        });

    }

    @Before
    public void setupCache(){
        cache = new WriteCache(UnpooledByteBufAllocator.DEFAULT, 10 * 1024);
        cache.put(0,0,entry);
        cache.put(0,1,lastEntry);
    }

    @Test
    public void testGetLastEntry(){
        boolean actual;
        try {

            actual = cache.getLastEntry(ledgerId).equals(lastEntry); //prendo ledger e entry dalla cache associata ad entry

        }catch (Exception e){
            actual = false;
            //ci entro se faccio get di qualcosa di cui non ho fatto put, oppure se uso indici <=0.
        }

        Assert.assertEquals(expected,actual);
    }

    @After
    public void tearDown(){
        cache.close();
    }
}

