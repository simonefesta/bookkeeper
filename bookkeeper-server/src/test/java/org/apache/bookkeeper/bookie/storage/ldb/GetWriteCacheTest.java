package org.apache.bookkeeper.bookie.storage.ldb;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import static org.junit.Assert.*;


@RunWith(Parameterized.class)
public class GetWriteCacheTest {



    private long ledgerId;
    private long entryId;
    private String expected;
    private ByteBuf entry;
    private WriteCache cache = null;

    



    //costruttore
    public GetWriteCacheTest(long ledgerId, long entryId, Integer entrySize, String expected){
        configure(ledgerId,entryId, entrySize, expected);
    }

    private void configure(long ledgerId, long entryId, Integer entrySize, String expected){
        this.ledgerId = ledgerId;
        this.entryId = entryId;
        if(entrySize != null)
             this.entry = Unpooled.wrappedBuffer(new byte[entrySize]);
        else this.entry = null;
        this.expected = expected;
    }

    @Parameterized.Parameters
    public static Collection<?> getTestParameters() {
        return Arrays.asList(new Object[][]{
                //   ledgerID     entryID      entrySize            expected
                {      0,              0,        1024,            "valid"       }, //valid
                {      0,              0,        1024,            "null"       }, //valid, but without 'put' in cache, invalid
                {      0,              0,        null,            "null"       },
                {      -1,            1,        1024,            "illegal"    },
                {      0,            -1,        1024,            "illegal"    },

                {      0,              -2,        1024,            "valid"       }, //valid


        });

    }


    @Before
    public void setupCache() {
        cache = new WriteCache(UnpooledByteBufAllocator.DEFAULT, 10 * 1024);

        if (Objects.equals(expected, "valid")){ // expected == valid
            try {
                cache.put(ledgerId, entryId, entry);
            } catch (Exception e) {
                Assert.fail("error setup test");

            }
        }
    }
    @Test
    public void testGetEntry() {
        ByteBuf actual;
        switch (expected) {
            case "valid":
                try {
                    actual = cache.get(ledgerId, entryId);
                    Assert.assertEquals(entry, actual);
                } catch (Exception e)
                {
                    Assert.fail("Fail in getWriteCacheTest case 'valid'");
                }
                break;

            case "null":
                try {
                    actual =cache.get(ledgerId, entryId);
                    assertNull(actual);

                } catch (Exception e)
                {
                    Assert.fail("Fail in getWriteCacheTest case 'null': expected 'null', got exception");

                }

                break;

            case "illegal":
                try {
                    cache.get(ledgerId, entryId);
                } catch ( IllegalArgumentException e) //ok
                {
                    assertTrue(true);
                }
                catch (NullPointerException e) {
                    Assert.fail("Fail in getWriteCacheTest case 'illegal'");
                }
                break;

        }
    }

    @After
    public void tearDown(){
        cache.close();
    }
}

