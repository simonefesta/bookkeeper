package org.apache.bookkeeper.bookie.storage.ldb;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;


@RunWith(Parameterized.class)
public class GetWriteCacheTest {



    private long ledgerId;
    private long entryId;

    private ByteBuf entry;
    private WriteCache cache = null;


    private Class<? extends Exception> expectedException;



    //costruttore
    public GetWriteCacheTest(long ledgerId, long entryId, ByteBuf entry,Class<? extends Exception> expectedException){
        configure(ledgerId,entryId, entry, expectedException);
    }

    private void configure(long ledgerId, long entryId, ByteBuf entry,Class<? extends Exception> expectedException){
        this.ledgerId = ledgerId;
        this.entryId = entryId;
        this.entry = entry;
        this.expectedException = expectedException;
    }

    @Parameterized.Parameters
    public static Collection<?> getTestParameters() {
        return Arrays.asList(new Object[][]{
                //   ledgerID     entryID      entry                                        exception
                {      0,              0,     Unpooled.wrappedBuffer(new byte[1024]),             null},
                {      -1,            -1,        null,                                   IllegalArgumentException.class }

        });

    }
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void setupCache(){
        cache = new WriteCache(UnpooledByteBufAllocator.DEFAULT, 10 * 1024);
        if(entry!= null) {
                cache.put(ledgerId,entryId, entry);
                }
    }

    @Test
    public void testGetEntry(){
        if (expectedException != null)
            exception.expect(expectedException);
        ByteBuf actual = cache.get(ledgerId,entryId);

        Assert.assertEquals(entry,actual);

    }

    @After
    public void tearDown(){
        cache.close();
    }
}

