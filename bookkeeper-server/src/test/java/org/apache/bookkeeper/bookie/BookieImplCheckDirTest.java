package org.apache.bookkeeper.bookie;

import org.junit.*;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;


import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import java.util.Collection;


import static org.powermock.api.mockito.PowerMockito.mock;

/* Test for public static void checkDirectoryStructure(File dir) by  org.apache.bookkeeper.bookie.bookieImpl.
Vediamo innanzitutto che il parametro richiesto è di tipo FILE, in particolare una directory.
Questo suggerisce come suite di test minimale : [istanza_valida, istanza_non valida, null]
 */
@RunWith(value = Enclosed.class)
public class BookieImplCheckDirTest {


   public static class BookieImplCheckDirYesMockTest {

        File mockFile;
        File mockParent;

        @Before
        public void setup() {

            mockFile = mock(File.class);
            //mockParent = mock(File.class);
            Mockito.doReturn(false).when(mockFile).exists();
            //Mockito.doReturn(mockParent).when(mockFile).getParentFile();
            Mockito.doReturn(false).when(mockFile).mkdirs();
        }

        @Test
        public void unableToCreateDirTest() //caso in cui non riesco a creare la directory
        {
            boolean actual = true;
            try {
                BookieImpl.checkDirectoryStructure(mockFile);
            } catch (NullPointerException | IOException e) {
                actual = false;
            }
            Assert.assertFalse(actual);

        }

    }

    @RunWith(Parameterized.class)
    public static class BookieImplNoMockPart1Test {

        private File directory;
        private boolean expected;
        private static File fileDirNotExists;

        public BookieImplNoMockPart1Test(boolean expected, File directory) {
            configure(expected, directory);
        }

        private void configure(boolean expected, File directory) {
            this.expected = expected;
            this.directory = directory;
        }

        @Parameterized.Parameters
        public static Collection<?> getTestParameters() {
            String path = "src/test/java/inner";
            fileDirNotExists = new File(path);
            fileDirNotExists.delete();
            return Arrays.asList(new Object[][]{
                    //   expected            directory
                    {true, new File("src/test/java")}, //dir exists
                    {false, null}, //dir è null
                    {true, fileDirNotExists},  //dir not exists anymore
            });
        }




        @Test
        public void testCheckDir() {
            boolean actual = true;
            try {
                BookieImpl.checkDirectoryStructure(directory);
            } catch (NullPointerException | IOException e) {
                actual = false;
            }
            Assert.assertEquals(expected, actual);

        }

        @AfterClass
        public static void teardown() {
            try{
                fileDirNotExists.delete();
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    @RunWith(JUnit4.class)
    public static class BookieImplNoMockPart2Test{

        private static File supportDir;
        private static final String[] extensions = {"txn", "idx", "log"};

        @BeforeClass
        public static void setup()  {
            supportDir = new File("src/test/java/testUpgrade");
            try {
                supportDir.mkdir();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                //throw new IOException("error setup test directory");
            }
        }

        @Test
        public void testUpgradeDir() throws IOException {
            boolean actual = false;

            for (String extension: extensions)
            {
                File supportFile = new File(supportDir+"/file."+extension);
                supportFile.createNewFile();
                File innerDir = new File(supportDir+"/inner");
                innerDir.delete();
                try{
                    BookieImpl.checkDirectoryStructure(innerDir);
                }
                catch (IOException e)
                {
                    actual = true;
                }

                Assert.assertTrue(actual);

                actual = false;
                supportFile.delete();
            }
        }

        @AfterClass
        public static void teardown() throws IOException {
            if(!supportDir.delete()) {
                throw new IOException("error deleting test directory");
            }
        }
    }


}
