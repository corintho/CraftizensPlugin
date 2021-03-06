CREATE TABLE `craftizens` (
  `npc_id` VARCHAR(12) NOT NULL,
  `npc_name` VARCHAR(30) NOT NULL,
  `posx` DOUBLE NOT NULL,
  `posy` DOUBLE NOT NULL,
  `posz` DOUBLE NOT NULL,
  `rotation` FLOAT NOT NULL,
  `pitch` FLOAT NOT NULL,
  PRIMARY KEY (`npc_id`)
);

CREATE TABLE `quests` (
  `id` VARCHAR(12) NOT NULL,
  `quest_name` TEXT NOT NULL,
  `start_npc` VARCHAR(12) NOT NULL,
  `end_npc` VARCHAR(12) NOT NULL,
  `items_provided` TEXT DEFAULT NULL,
  `rewards` TEXT DEFAULT NULL,
  `quest_desc` MEDIUMTEXT NOT NULL,
  `quest_type` VARCHAR(10) NOT NULL,
  `prereq` VARCHAR(12) DEFAULT NULL,
  `location` TEXT DEFAULT NULL,
  `data` TEXT DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `quests_active` (
  `player_name` VARCHAR(25) NOT NULL,
  `quest_id` VARCHAR(12) NOT NULL,
  `progress` VARCHAR(50) DEFAULT NULL,
  PRIMARY KEY (`player_name`,`quest_id`)
);

CREATE TABLE `quests_completed` (
  `player_name` VARCHAR(25) NOT NULL,
  `quest_id` VARCHAR(12) NOT NULL,
  `date_completed` TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  PRIMARY KEY (`player_name`,`quest_id`)
);

-- v0.2 update

ALTER TABLE `craftizens` ADD COLUMN `item_in_hand` INT DEFAULT 0 NOT NULL;

-- v0.5 update

ALTER TABLE `quests` ADD COLUMN `completion_text` TEXT DEFAULT NULL;

CREATE TABLE `craftizens_dialog` (
  `npc_id` VARCHAR(12) NOT NULL,
  `dialog_id` VARCHAR(12) NOT NULL,
  `dialog_text` TEXT NOT NULL,
  PRIMARY KEY (`npc_id`,`dialog_id`)
);

-- v0.6 update

ALTER TABLE `craftizens` ADD COLUMN `route_type` VARCHAR(8) DEFAULT NULL;
ALTER TABLE `craftizens` ADD COLUMN `route` TEXT DEFAULT NULL;

-- v0.7 update
ALTER TABLE `quests` ADD COLUMN `rankreq` VARCHAR(12) DEFAULT NULL;
ALTER TABLE `quests` ADD COLUMN `rankreward` VARCHAR(12) DEFAULT NULL;

-- v0.7.1 update
ALTER TABLE `quests` ADD COLUMN `cost` VARCHAR(12) DEFAULT '0';
ALTER TABLE `quests` ADD COLUMN `prize` VARCHAR(12) DEFAULT '0';
