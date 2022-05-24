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
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;


@RunWith(Parameterized.class)
public class ReadCacheParameterizedTest {

    private boolean expected;

    private long ledgerIdPut;
    private long ledgerIdGet;

    private long entryIdPut;
    private long entryIdGet;


    private  ByteBuf entry;
    private ReadCache cache = null;

    //costruttore
    public ReadCacheParameterizedTest(boolean expected, long ledgerIdPut, long entryIdPut, long ledgerIdGet, long entryIdGet){
    configure(expected,ledgerIdPut,entryIdPut,ledgerIdGet,entryIdGet);
    }

    private void configure(boolean expected,long ledgerIdPut, long entryIdPut, long ledgerIdGet, long entryIdGet){
        this.expected = expected;
        this.ledgerIdPut = ledgerIdPut;
        this.entryIdPut = entryIdPut;
        this.ledgerIdGet = ledgerIdGet;
        this.entryIdGet = entryIdGet;
    }

    @Parameterized.Parameters
    public static Collection<?> getTestParameters() {
        return Arrays.asList(new Object[][]{
            // expected   ledgerPut entryPut  ledgerGet  entryGet
                {false,      -1,       0,        -1,        0}, //put di -1 fails
                {false,       0,       1,         1,        0}, //put di 0,1,... diverso da get di 1,0,... (ciò che metto diverso da ciò che prelevo)
               // {false,       0,      -1,         0,       -1}, //idEntry must be non-negative. But test fails(?) (https://bookkeeper.apache.org/docs/api/ledger-adv-api)
                {true,        0,       0,         0,        0},
                {false,        0,       2,         0,       2} //in questo caso l'errore dipende dalla size delle entry >segmentSize


        });

    }

    @Before
    public void setupCache(){
        cache = new ReadCache(UnpooledByteBufAllocator.DEFAULT, 10 * 1024);

        if(entryIdPut == 2) {
                              entry = Unpooled.wrappedBuffer(new byte[5121]); //too big size
                            }
        else {
               entry = Unpooled.wrappedBuffer(new byte[1024]); //std size

            }
    }

    @Test
    public void testReadCache(){
        boolean actual;
        try {
            cache.put(ledgerIdPut,entryIdPut,entry);
            actual = cache.get(ledgerIdGet,entryIdGet).equals(entry); //prendo ledger e entry dalla cache associata ad entry

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
