package pl.pastebin.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.pastebin.model.Metadata;

@Repository
public interface MetadataRepos extends JpaRepository<Metadata, Integer> {
    Metadata findByHash(String hash);
}
