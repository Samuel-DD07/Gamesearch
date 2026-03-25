package fr.epita.apping.fullstack.gamesearch.data.loader;

import fr.epita.apping.fullstack.gamesearch.data.model.*;
import fr.epita.apping.fullstack.gamesearch.data.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;
    private final PlatformRepository platformRepository;
    private final TagRepository tagRepository;
    private final PartnerRepository partnerRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (gameRepository.count() > 0) {
            return;
        }

        // Genres
        GenreModel rpg = saveGenre("RPG");
        GenreModel action = saveGenre("Action");
        GenreModel souls = saveGenre("Souls-like");
        GenreModel adventure = saveGenre("Adventure");
        GenreModel strategy = saveGenre("Strategy");

        // Platforms
        PlatformModel pc = savePlatform("PC");
        PlatformModel ps5 = savePlatform("PS5");
        PlatformModel xbox = savePlatform("Xbox Series X/S");
        savePlatform("Nintendo Switch");

        // Tags
        TagModel openWorld = saveTag("Open World");
        TagModel storyRich = saveTag("Story Rich");

        // Partner
        PartnerModel defaultPartner = partnerRepository.save(PartnerModel.builder()
                .name("Default Partner")
                .apiKeyHash("default-api-key-hash")
                .active(true)
                .build());

        // Games
        saveGame("The Witcher 3: Wild Hunt", 2015, "CD Projekt Red", 9.8f,
                "The Witcher: Wild Hunt is a story-driven open world RPG set in a visually stunning fantasy universe full of meaningful choices and impactful consequences.",
                "https://cdn.akamai.steamstatic.com/steam/apps/292030/header.jpg",
                List.of(rpg, action, adventure), List.of(pc, ps5, xbox), List.of(openWorld, storyRich), defaultPartner);

        saveGame("Elden Ring", 2022, "FromSoftware", 9.7f,
                "Rise, Tarnished, and be guided by grace to brandish the power of the Elden Ring and become an Elden Lord in the Lands Between.",
                "https://cdn.akamai.steamstatic.com/steam/apps/1245620/header.jpg",
                List.of(rpg, action, souls), List.of(pc, ps5, xbox), List.of(openWorld), defaultPartner);

        saveGame("Baldur's Gate 3", 2023, "Larian Studios", 9.9f,
                "Gather your party, and return to the Forgotten Realms in a tale of fellowship and betrayal, sacrifice and survival, and the lure of absolute power.",
                "https://cdn.akamai.steamstatic.com/steam/apps/1086940/header.jpg",
                List.of(rpg, strategy), List.of(pc, ps5), List.of(storyRich), defaultPartner);

        saveGame("Cyberpunk 2077", 2020, "CD Projekt Red", 8.6f,
                "Cyberpunk 2077 is an open-world, action-adventure RPG set in the megalopolis of Night City, where you play as a cyberpunk mercenary wrapped up in a do-or-die fight for survival.",
                "https://cdn.akamai.steamstatic.com/steam/apps/1091500/header.jpg",
                List.of(rpg, action), List.of(pc, ps5, xbox), List.of(openWorld, storyRich), defaultPartner);

        saveGame("Hogwarts Legacy", 2023, "Avalanche Software", 8.5f,
                "Hogwarts Legacy is an immersive, open-world action RPG set in the world first introduced in the Harry Potter books.",
                "https://cdn.akamai.steamstatic.com/steam/apps/1549970/header.jpg",
                List.of(rpg, action, adventure), List.of(pc, ps5, xbox), List.of(openWorld), defaultPartner);
    }

    private GenreModel saveGenre(String name) {
        return genreRepository.findByName(name)
                .orElseGet(() -> genreRepository.save(GenreModel.builder().name(name).build()));
    }

    private PlatformModel savePlatform(String name) {
        return platformRepository.findByName(name)
                .orElseGet(() -> platformRepository.save(PlatformModel.builder().name(name).build()));
    }

    private TagModel saveTag(String name) {
        return tagRepository.findByName(name)
                .orElseGet(() -> tagRepository.save(TagModel.builder().name(name).build()));
    }

    private void saveGame(String title, int year, String publisher, float rating,
            String description, String coverUrl,
            List<GenreModel> genres, List<PlatformModel> platforms,
            List<TagModel> tags, PartnerModel partner) {
        gameRepository.save(GameModel.builder()
                .externalId(UUID.randomUUID().toString())
                .title(title)
                .releaseYear(year)
                .publisher(publisher)
                .rating(rating)
                .description(description)
                .coverUrl(coverUrl)
                .genres(genres)
                .platforms(platforms)
                .tags(tags)
                .partner(partner)
                .build());
    }
}
