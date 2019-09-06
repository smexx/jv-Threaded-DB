import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Class which represents this "data storage" of yours. You can even have multiple instances of it simultaneously if you need
    class Database {
        //private static final Logger LOG = LoggerFactory.getLogger(Database.class);
        //Some data the DB contains
        private CopyOnWriteArrayList<String> entities = new CopyOnWriteArrayList<String>();

        //Way to read the stored data
        public CopyOnWriteArrayList<String> getEntities() {
            return entities;
        }

        public Object getSpecificEntities(int i){
            return entities.get(i);
        }

        //Way to add new data
        public void addEntity(Object newObject) {
            entities.add((String) newObject);
        }

        public Iterator<String> iterator() {
            return entities.iterator();
        }
}

    //Some working class, which fills the database with information
    class EntityCreator {
        //private static final Logger LOG = LoggerFactory.getLogger(EntityCreator.class);
        private final Database db;

        //Each working class attaches itself to specific database instance. Therefore, you need to pass that instance through the constructor and save it for later job
        public EntityCreator(Database operationalDb) {
            this.db = operationalDb;
        }

        public void writeSomeData(Object s) {
            db.addEntity(s);
        }
    }

    //Some working class, which depends on information saved in database
    class EntityReader {
        private static final Logger LOG = LoggerFactory.getLogger(EntityReader.class);
        private final Database db;

        //Each working class attaches itself to specific database instance. Therefore, you need to pass that instance through the constructor and save it for later job
        public EntityReader(Database operationalDb) {
            this.db = operationalDb;
        }

        public CopyOnWriteArrayList<String> doSomeWork() {
            /*for (Object e : db.getEntities()) {
                print(e);
            }

             */
            return db.getEntities();
        }
        public Object lastEntity(){
            int i;
            i = (db.getEntities().size() - 1 );
            LOG.debug(String.valueOf(i));
            Object o = db.getSpecificEntities(i);
            return o;
        }

        private void print(Object e) {
            LOG.info((String) e);
        }
    }
    class testSync implements Runnable{
        private final EntityCreator c;
        private final EntityReader r;

        public testSync(EntityReader r, EntityCreator c) {
            this.c = c;
            this.r = r;
        }

        @Override
        public void run() {
            Thread t = Thread.currentThread();
            System.out.println("In thread id "+ t.getId());
            for(int i = 0; i < 1000; i++){
                StringBuilder sb = new StringBuilder();
                sb.append(System.currentTimeMillis());
                String strI = sb.toString();
                c.writeSomeData(strI);
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class MainClass {
        private static final Logger LOG = LoggerFactory.getLogger(MainClass.class);
        public static void main(String[] args) {
            Database orderbookdb = new Database();
            Database tradesdb = new Database();
            EntityCreator orderbookDBCreator = new EntityCreator(orderbookdb);
            EntityCreator tradesDBCreator = new EntityCreator(tradesdb);
            EntityReader orderbookDBReader = new EntityReader(orderbookdb);
            EntityReader tradesDBReader = new EntityReader(tradesdb);

            
            Thread t1 = new Thread(new testSync(orderbookDBReader, orderbookDBCreator));
            Thread t2 = new Thread(new testSync(orderbookDBReader, orderbookDBCreator));
            Thread t3 = new Thread(new testSync(orderbookDBReader, orderbookDBCreator));
            Thread t4 = new Thread(new testSync(orderbookDBReader, orderbookDBCreator));

            t1.start();
            t2.start();
            t3.start();
            t4.start();

            try {
                t1.join();
                t2.join();
                t3.join();
                t4.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LOG.info("Size of list is " + (orderbookDBReader.doSomeWork().size()));
            for(String i : orderbookdb.getEntities()){
                System.out.println("List Element - " + i);
            }
        }


    }

