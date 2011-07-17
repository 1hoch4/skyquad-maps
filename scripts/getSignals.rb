#!/usr/bin/ruby

require 'json'
require 'optparse'
require 'iconv'

class Parser
	class Dataset
		attr_accessor :name, :signals, :sendid
		def initialize()
			@signals = []
		end

		def to_json(*a)
			return {'dataset' => @name,
				'sendid'  => @sendid, 
				'signals' => @signals
			}.to_json(*a)
		end
	end

	class Signal
		attr_accessor :name, :type, :unit, :factor
		def to_json(*a)
			return {'name'  => @name,
				'unit'  => @unit,
				'factor'=> @factor,
				'type'  => @type
			}.to_json(*a)
		end
	end

	def initialize()
		@sets = []
		@set = nil
	end

	def parse(file)
		@state = :close
		file_handle = File.open(file)
		# To handle invalid byte sequence right.
		ic = Iconv.new('UTF-8//IGNORE', 'UTF-8')
		
		while(input = file_handle.gets)
			line = ic.iconv(input)
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
						# dirty cut of expression which are not var names
						signal.name = $1.sub(/[^A-z0-9_\[\]]+.*$/, "") 
						signal.name =~ /_([ilcus]{2})/
						signal.type = case $1
							      	when /sc/ then "CHAR"
							      	when /uc/ then "UCHAR"
							      	when /si/ then "INT"
							      	when /ui/ then "UINT"
							      	when /sl/ then "LONG"
							      	else "INT" 
							      end
						
						/.*Unit:#([^#]+)#.*/ =~ line
						# Empty unit field
						signal.unit = ($1 == nil ? "-" : $1)
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
		file_handle.close()
		return @sets
	end
end

pretty_print = false
optparse = OptionParser.new do|opts|
	opts.banner = <<-EOF
Usage: #{File.basename $0} [options] source1.c source2.c ..."
Parse communication.c of the Skyquad source code and
generates json, which represent its protocol.
	EOF

	opts.on( '-pp', '--prettyprint', 'Prints json human readable' ) do
		pretty_print = true
	end

	opts.on( '-h', '--help', 'Display this screen' ) do
		puts opts
		exit
	end
end
optparse.parse!

ARGV.each do |file|
	if !File.exist?(file) then
		puts "'Skip file #{file}: it does not exist!'"
		next
	end
	p = Parser.new
	sets = p.parse(file)
	if pretty_print then 
		puts JSON.pretty_generate(sets)
	else
		puts JSON.dump(sets)
	end
end
#vim: foldmethod=marker:filetype=ruby:expandtab:shiftwidth=2:tabstop=2:softtabstop=2:textwidth=80
