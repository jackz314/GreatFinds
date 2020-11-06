package em;

import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;

@Stateless
public class EntityManagerProducer {

  //inject from persistence.xml config, only possible with ejb support
//  @PersistenceContext(unitName="greatFindsMySQL")
  private EntityManager entityManager;

  @Produces
  public EntityManager entityManager(){
    if(entityManager == null) entityManager = EMF.createEntityManager();
    return entityManager;
  }

}