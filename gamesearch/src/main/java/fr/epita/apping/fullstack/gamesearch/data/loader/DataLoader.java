package fr.epita.apping.fullstack.gamesearch.data.loader;

import fr.epita.apping.fullstack.gamesearch.data.model.*;
import fr.epita.apping.fullstack.gamesearch.data.repository.*;
<<<<<<< HEAD
import java.util.List;
import java.util.Optional;
import java.util.UUID;
=======
>>>>>>> 34dd8d463ca2e8a82a8990771d31e71b2064d270
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

<<<<<<< HEAD
=======
import java.util.List;
import java.util.UUID;

>>>>>>> 34dd8d463ca2e8a82a8990771d31e71b2064d270
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

<<<<<<< HEAD
  private final GameRepository gameRepository;
  private final GenreRepository genreRepository;
  private final PlatformRepository platformRepository;
  private final TagRepository tagRepository;
  private final PartnerRepository partnerRepository;

  @Override
  @Transactional
  public void run(String... args) throws Exception {
    GenreModel rpg = saveGenre("RPG");
    GenreModel action = saveGenre("Action");
    GenreModel souls = saveGenre("Souls-like");
    GenreModel adventure = saveGenre("Adventure");
    GenreModel strategy = saveGenre("Strategy");
    GenreModel fighting = saveGenre("Fighting");
    GenreModel horror = saveGenre("Horror");
    GenreModel survival = saveGenre("Survival");

    PlatformModel pc = savePlatform("PC");
    PlatformModel ps5 = savePlatform("PS5");
    PlatformModel xbox = savePlatform("Xbox Series X/S");

    TagModel openWorld = saveTag("Open World");
    TagModel storyRich = saveTag("Story Rich");
    TagModel multiplayer = saveTag("Multiplayer");

    PartnerModel defaultPartner =
        partnerRepository
            .findByName("Default Partner")
            .orElseGet(
                () ->
                    partnerRepository.save(
                        PartnerModel.builder()
                            .name("Default Partner")
                            .apiKeyHash("default-api-key-hash")
                            .active(true)
                            .build()));

    saveGame(
        "The Witcher 3: Wild Hunt",
        2015,
        "CD Projekt Red",
        9.8f,
        "The Witcher: Wild Hunt is a story-driven open world RPG set in a visually stunning fantasy universe full of meaningful choices and impactful consequences.",
        "https://cdn.akamai.steamstatic.com/steam/apps/292030/header.jpg",
        List.of(rpg, action, adventure),
        List.of(pc, ps5, xbox),
        List.of(openWorld, storyRich),
        defaultPartner);

    saveGame(
        "Elden Ring",
        2022,
        "FromSoftware",
        9.7f,
        "Rise, Tarnished, and be guided by grace to brandish the power of the Elden Ring and become an Elden Lord in the Lands Between.",
        "https://cdn.akamai.steamstatic.com/steam/apps/1245620/header.jpg",
        List.of(rpg, action, souls),
        List.of(pc, ps5, xbox),
        List.of(openWorld),
        defaultPartner);

    saveGame(
        "Baldur's Gate 3",
        2023,
        "Larian Studios",
        9.9f,
        "Gather your party, and return to the Forgotten Realms in a tale of fellowship and betrayal, sacrifice and survival, and the lure of absolute power.",
        "https://cdn.akamai.steamstatic.com/steam/apps/1086940/header.jpg",
        List.of(rpg, strategy),
        List.of(pc, ps5),
        List.of(storyRich),
        defaultPartner);

    saveGame(
        "Cyberpunk 2077",
        2020,
        "CD Projekt Red",
        8.6f,
        "Cyberpunk 2077 is an open-world, action-adventure RPG set in the megalopolis of Night City, where you play as a cyberpunk mercenary wrapped up in a do-or-die fight for survival.",
        "https://cdn.akamai.steamstatic.com/steam/apps/1091500/header.jpg",
        List.of(rpg, action),
        List.of(pc, ps5, xbox),
        List.of(openWorld, storyRich),
        defaultPartner);

    saveGame(
        "Hogwarts Legacy",
        2023,
        "Avalanche Software",
        8.5f,
        "Hogwarts Legacy is an immersive, open-world action RPG set in the world first introduced in the Harry Potter books.",
        "https://cdn.akamai.steamstatic.com/steam/apps/1549970/header.jpg",
        List.of(rpg, action, adventure),
        List.of(pc, ps5, xbox),
        List.of(openWorld),
        defaultPartner);

    saveGame(
        "Red Dead Redemption 2",
        2018,
        "Rockstar Games",
        9.7f,
        "America, 1899. Arthur Morgan and the Van der Linde gang are outlaws on the run.",
        "https://cdn.akamai.steamstatic.com/steam/apps/1174180/header.jpg",
        List.of(rpg, action, adventure),
        List.of(pc, ps5, xbox),
        List.of(openWorld, storyRich),
        defaultPartner);

    saveGame(
        "The Last of Us Part II",
        2020,
        "Naughty Dog",
        9.3f,
        "Five years after their dangerous journey across the post-pandemic United States, Ellie and Joel have settled down in Jackson, Wyoming.",
        "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1888530/header.jpg",
        List.of(action, adventure),
        List.of(pc, ps5),
        List.of(storyRich),
        defaultPartner);

    saveGame(
        "God of War Ragnarök",
        2022,
        "Santa Monica Studio",
        9.4f,
        "Kratos and Atreus must journey to each of the Nine Realms in search of answers.",
        "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2322010/header.jpg",
        List.of(action, adventure),
        List.of(pc, ps5),
        List.of(storyRich),
        defaultPartner);

    saveGame(
        "Starfield",
        2023,
        "Bethesda Game Studios",
        8.2f,
        "Starfield is the first new universe in 25 years from Bethesda Game Studios.",
        "https://cdn.akamai.steamstatic.com/steam/apps/1716740/header.jpg",
        List.of(rpg),
        List.of(pc, xbox),
        List.of(openWorld, storyRich),
        defaultPartner);

    saveGame(
        "Final Fantasy XVI",
        2023,
        "Square Enix",
        8.7f,
        "An epic dark fantasy world where the fate of the land is decided by the mighty Eikons.",
        "https://gaming-cdn.com/images/products/12586/orig/final-fantasy-xvi-pc-game-steam-cover.jpg",
        List.of(rpg, action),
        List.of(ps5),
        List.of(storyRich),
        defaultPartner);

    saveGame(
        "Diablo IV",
        2023,
        "Blizzard Entertainment",
        8.4f,
        "The endless battle between the High Heavens and the Burning Hells rages on.",
        "https://cdn.akamai.steamstatic.com/steam/apps/2344520/header.jpg",
        List.of(rpg, action),
        List.of(pc, ps5, xbox),
        List.of(multiplayer),
        defaultPartner);

    saveGame(
        "Marvel's Spider-Man 2",
        2023,
        "Insomniac Games",
        9.0f,
        "Spider-Men Peter Parker and Miles Morales return for an exciting new adventure.",
        "https://gaming-cdn.com/images/products/14588/orig/marvel-s-spider-man-2-pc-game-steam-cover.jpg",
        List.of(action, adventure),
        List.of(ps5),
        List.of(openWorld),
        defaultPartner);

    saveGame(
        "Street Fighter 6",
        2023,
        "Capcom",
        9.2f,
        "Powered by Capcom’s proprietary RE ENGINE, newest entry in the legendary series.",
        "https://cdn.akamai.steamstatic.com/steam/apps/1364780/header.jpg",
        List.of(fighting, action),
        List.of(pc, ps5, xbox),
        List.of(multiplayer),
        defaultPartner);

    saveGame(
        "Resident Evil 4 Remake",
        2023,
        "Capcom",
        9.5f,
        "Survival is just the beginning. Six years have passed since the biological disaster in Raccoon City.",
        "https://cdn.akamai.steamstatic.com/steam/apps/2050650/header.jpg",
        List.of(action, horror, survival),
        List.of(pc, ps5, xbox),
        List.of(storyRich),
        defaultPartner);

    saveGame(
        "Star Wars Jedi: Survivor",
        2023,
        "Respawn Entertainment",
        8.6f,
        "The story of Cal Kestis continues in Star Wars Jedi: Survivor.",
        "https://cdn.akamai.steamstatic.com/steam/apps/1774580/header.jpg",
        List.of(action, adventure),
        List.of(pc, ps5, xbox),
        List.of(storyRich),
        defaultPartner);
  }

  private GenreModel saveGenre(String name) {
    return genreRepository
        .findByName(name)
        .orElseGet(() -> genreRepository.save(GenreModel.builder().name(name).build()));
  }

  private PlatformModel savePlatform(String name) {
    return platformRepository
        .findByName(name)
        .orElseGet(() -> platformRepository.save(PlatformModel.builder().name(name).build()));
  }

  private TagModel saveTag(String name) {
    return tagRepository
        .findByName(name)
        .orElseGet(() -> tagRepository.save(TagModel.builder().name(name).build()));
  }

  private void saveGame(
      String title,
      int year,
      String publisher,
      float rating,
      String description,
      String coverUrl,
      List<GenreModel> genres,
      List<PlatformModel> platforms,
      List<TagModel> tags,
      PartnerModel partner) {

    Optional<GameModel> existing = gameRepository.findByTitle(title);
    if (existing.isPresent()) {
      GameModel game = existing.get();
      if (!game.getCoverUrl().equals(coverUrl)) {
        game.setCoverUrl(coverUrl);
        gameRepository.save(game);
      }
      return;
    }

    gameRepository.save(
        GameModel.builder()
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
=======
        private final GameRepository gameRepository;
        private final GenreRepository genreRepository;
        private final PlatformRepository platformRepository;
        private final TagRepository tagRepository;
        private final PartnerRepository partnerRepository;

        @Override
        @Transactional
        public void run(String... args) throws Exception {
                GenreModel rpg = saveGenre("RPG");
                GenreModel action = saveGenre("Action");
                GenreModel souls = saveGenre("Souls-like");
                GenreModel adventure = saveGenre("Adventure");
                GenreModel strategy = saveGenre("Strategy");
                GenreModel fighting = saveGenre("Fighting");
                GenreModel horror = saveGenre("Horror");
                GenreModel survival = saveGenre("Survival");

                PlatformModel pc = savePlatform("PC");
                PlatformModel ps5 = savePlatform("PS5");
                PlatformModel xbox = savePlatform("Xbox Series X/S");

                TagModel openWorld = saveTag("Open World");
                TagModel storyRich = saveTag("Story Rich");
                TagModel multiplayer = saveTag("Multiplayer");

                PartnerModel defaultPartner = partnerRepository.findByName("Default Partner")
                                .orElseGet(() -> partnerRepository.save(PartnerModel.builder()
                                                .name("Default Partner")
                                                .apiKeyHash("default-api-key-hash")
                                                .active(true)
                                                .build()));

                saveGame("The Witcher 3: Wild Hunt", 2015, "CD Projekt Red", 9.8f,
                                "The Witcher: Wild Hunt is a story-driven open world RPG set in a visually stunning fantasy universe full of meaningful choices and impactful consequences.",
                                "https://cdn.akamai.steamstatic.com/steam/apps/292030/header.jpg",
                                List.of(rpg, action, adventure), List.of(pc, ps5, xbox), List.of(openWorld, storyRich),
                                defaultPartner);

                saveGame("Elden Ring", 2022, "FromSoftware", 9.7f,
                                "Rise, Tarnished, and be guided by grace to brandish the power of the Elden Ring and become an Elden Lord in the Lands Between.",
                                "https://cdn.akamai.steamstatic.com/steam/apps/1245620/header.jpg",
                                List.of(rpg, action, souls), List.of(pc, ps5, xbox), List.of(openWorld),
                                defaultPartner);

                saveGame("Baldur's Gate 3", 2023, "Larian Studios", 9.9f,
                                "Gather your party, and return to the Forgotten Realms in a tale of fellowship and betrayal, sacrifice and survival, and the lure of absolute power.",
                                "https://cdn.akamai.steamstatic.com/steam/apps/1086940/header.jpg",
                                List.of(rpg, strategy), List.of(pc, ps5), List.of(storyRich), defaultPartner);

                saveGame("Cyberpunk 2077", 2020, "CD Projekt Red", 8.6f,
                                "Cyberpunk 2077 is an open-world, action-adventure RPG set in the megalopolis of Night City, where you play as a cyberpunk mercenary wrapped up in a do-or-die fight for survival.",
                                "https://cdn.akamai.steamstatic.com/steam/apps/1091500/header.jpg",
                                List.of(rpg, action), List.of(pc, ps5, xbox), List.of(openWorld, storyRich),
                                defaultPartner);

                saveGame("Hogwarts Legacy", 2023, "Avalanche Software", 8.5f,
                                "Hogwarts Legacy is an immersive, open-world action RPG set in the world first introduced in the Harry Potter books.",
                                "https://cdn.akamai.steamstatic.com/steam/apps/1549970/header.jpg",
                                List.of(rpg, action, adventure), List.of(pc, ps5, xbox), List.of(openWorld),
                                defaultPartner);

                saveGame("Red Dead Redemption 2", 2018, "Rockstar Games", 9.7f,
                                "America, 1899. Arthur Morgan and the Van der Linde gang are outlaws on the run.",
                                "https://cdn.akamai.steamstatic.com/steam/apps/1174180/header.jpg",
                                List.of(rpg, action, adventure), List.of(pc, ps5, xbox), List.of(openWorld, storyRich),
                                defaultPartner);

                saveGame("The Last of Us Part II", 2020, "Naughty Dog", 9.3f,
                                "Five years after their dangerous journey across the post-pandemic United States, Ellie and Joel have settled down in Jackson, Wyoming.",
                                "https://image.api.playstation.com/vulcan/img/rnd/202010/2618/itB46V9itpRs0p6N7itB46V9.png",
                                List.of(action, adventure), List.of(ps5), List.of(storyRich), defaultPartner);

                saveGame("God of War Ragnarök", 2022, "Santa Monica Studio", 9.4f,
                                "Kratos and Atreus must journey to each of the Nine Realms in search of answers.",
                                "https://image.api.playstation.com/vulcan/img/rnd/202207/1210/487v87Sv87S.png",
                                List.of(action, adventure), List.of(ps5), List.of(storyRich), defaultPartner);

                saveGame("Starfield", 2023, "Bethesda Game Studios", 8.2f,
                                "Starfield is the first new universe in 25 years from Bethesda Game Studios.",
                                "https://cdn.akamai.steamstatic.com/steam/apps/1716740/header.jpg",
                                List.of(rpg), List.of(pc, xbox), List.of(openWorld, storyRich), defaultPartner);

                saveGame("Final Fantasy XVI", 2023, "Square Enix", 8.7f,
                                "An epic dark fantasy world where the fate of the land is decided by the mighty Eikons.",
                                "https://image.api.playstation.com/vulcan/img/rnd/202211/0708/1R1R1R.png",
                                List.of(rpg, action), List.of(ps5), List.of(storyRich), defaultPartner);

                saveGame("Diablo IV", 2023, "Blizzard Entertainment", 8.4f,
                                "The endless battle between the High Heavens and the Burning Hells rages on.",
                                "https://cdn.akamai.steamstatic.com/steam/apps/2344520/header.jpg",
                                List.of(rpg, action), List.of(pc, ps5, xbox), List.of(multiplayer), defaultPartner);

                saveGame("Marvel's Spider-Man 2", 2023, "Insomniac Games", 9.0f,
                                "Spider-Men Peter Parker and Miles Morales return for an exciting new adventure.",
                                "https://image.api.playstation.com/vulcan/img/rnd/202306/0819/2V2V2V.png",
                                List.of(action, adventure), List.of(ps5), List.of(openWorld), defaultPartner);

                saveGame("Street Fighter 6", 2023, "Capcom", 9.2f,
                                "Powered by Capcom’s proprietary RE ENGINE, newest entry in the legendary series.",
                                "https://cdn.akamai.steamstatic.com/steam/apps/1364780/header.jpg",
                                List.of(fighting, action), List.of(pc, ps5, xbox), List.of(multiplayer),
                                defaultPartner);

                saveGame("Resident Evil 4 Remake", 2023, "Capcom", 9.5f,
                                "Survival is just the beginning. Six years have passed since the biological disaster in Raccoon City.",
                                "https://cdn.akamai.steamstatic.com/steam/apps/2050650/header.jpg",
                                List.of(action, horror, survival), List.of(pc, ps5, xbox), List.of(storyRich),
                                defaultPartner);

                saveGame("Star Wars Jedi: Survivor", 2023, "Respawn Entertainment", 8.6f,
                                "The story of Cal Kestis continues in Star Wars Jedi: Survivor.",
                                "https://cdn.akamai.steamstatic.com/steam/apps/1774580/header.jpg",
                                List.of(action, adventure), List.of(pc, ps5, xbox), List.of(storyRich), defaultPartner);
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

                if (gameRepository.findByTitle(title).isPresent()) {
                        return;
                }

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
>>>>>>> 34dd8d463ca2e8a82a8990771d31e71b2064d270
}
