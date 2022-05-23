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
    public class hasEntryReadCacheTest {

        private boolean expected;

        private long ledgerId;
        private long entryId;


        private final ByteBuf entry = Unpooled.wrappedBuffer(new byte[1024]);
        private ReadCache cache = null;

        //costruttore
        public hasEntryReadCacheTest(boolean expected, long ledgerId, long entryId){
            configure(expected,ledgerId,entryId);
        }

        private void configure(boolean expected, long ledgerId, long entryId){
            this.expected = expected;
            this.ledgerId = ledgerId;
            this.entryId = entryId;
        }

        @Parameterized.Parameters
        public static Collection<?> getTestParameters() {
            return Arrays.asList(new Object[][]{
                    // expected     ledger      entry
                     {false,            -1,       0    }, //put di -1 fails
                     {false,            1,       -1    }, //put di -1 fails
                     {true,              1,       1    }, //nel setup ci metto una entry
                     {false,             1,       0    }, //nel setup non ci ho messo una entry



            });

        }

        @Before
        public void setupCache(){
            cache = new ReadCache(UnpooledByteBufAllocator.DEFAULT, 10 * 1024);
            cache.put(1,1,entry);
            //cache.put(-1,-1,entry); Keys and values must be >= 0
            //cache.put(-1,0,entry);
            //cache.put(0,-1,entry); Keys and values must be >= 0
        }

        @Test
        public void testReadCache(){
            boolean actual;
            try {
                actual = cache.hasEntry(ledgerId,entryId);
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

