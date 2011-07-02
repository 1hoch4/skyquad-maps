#!/usr/bin/ruby

class Parser
	class Dataset
		attr_accessor :name, :signals, :sendid
		def initialize()
			@signals = []
		end
		def to_s()
		return <<EOF
	// Send-ID #{@sendid}
	new SignalGroup(#{@name},
	new Signal[]{
#{@signals.join.chomp}
	}),
EOF
		end
	end

	class Signal
		attr_accessor :name, :type, :unit, :factor
		def to_s()
			return <<-EOF 
		new Signal("#{@name}", "#{@unit}", #{@factor}, Signal.Size.#{@type}),
			EOF
		end
	end
	def initialize()
		@state = :close
		@sets = []
		@set = nil
	end

	def parse(file)
		@state = :close
		File.readlines(file).each do |line|
			case @state
				when :close 
					next unless (line =~ /.*#Dataset_START#.*/)
					@set = Dataset.new
					@state = :open
				when :open
					case line
					when /.*#Dataset_END#.*/
						@sets.push(@set)
						@state = :close
						next
					when /Datasetname:#([^#]+)#/
						@set.name = $1
						@set.sendid = @sets.length + 1
					when /.*uart_write_(?:int|long)\(([^)]+)\)/
						signal = Signal.new
						signal.name = $1.sub!(/_([^_]+)$/, '')
						signal.type = case $1
							      	when /sc/ then "CHAR"
							      	when /uc/ then "UCHAR"
							      	when /si/ then "INT"
							      	when /ui/ then "UINT"
							      	when /sl/ then "LONG"
							      	else "INT"
							      end
						
						/.*Unit:#([^#]+)#.*/ =~ line
						signal.unit = $1
						/.*Factor:#([^#]+)#.*/ =~ line
						factor = $1.tr(",", ".") 
						/.*Divisor:#([^#]+)#.*/ =~ line
						divisor= $1.tr(",", ".")
						signal.factor = factor.to_f / divisor.to_f

						#offset = /.*Offset:#([^#]+)#.*/.match(line)
						
						@set.signals.push(signal)
					end
			end
		end
			return @sets
	end
end

if ARGV.empty? then
	puts <<-EOF
Parse communication.c of the Skyquad source code 
and generates java code, which represent its protocol 
usage:
	#{File.basename $0} communication.c
	EOF
end

ARGV.each do |file|
	if !File.exist?(file) then
		puts "'Skip #{file}: it does not exist!'"
		next
	end
	p = Parser.new
	sets = p.parse(file)
	puts <<EOF
public static final SignalGroup[] Db = new SignalGroup[]{"
#{sets.join}
};
EOF
end
#vim: foldmethod=marker:filetype=ruby:expandtab:shiftwidth=2:tabstop=2:softtabstop=2:textwidth=80
